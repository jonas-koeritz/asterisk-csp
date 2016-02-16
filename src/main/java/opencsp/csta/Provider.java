package opencsp.csta;

import io.netty.channel.Channel;
import opencsp.Log;
import opencsp.asterisk.Asterisk;
import opencsp.csta.messages.*;
import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlEncoder;
import opencsp.csta.xml.CSTAXmlSerializable;
import opencsp.tcp.CSTATcpMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Provider {
    private static final String TAG = "Provider";

    private List<Device> devices;
    private List<Connection> connections;
    private List<Call> calls;
    private CSTASessionManager sessionManager;

    private String countryCode;
    private String areaCode;
    private String systemPrefix;

    private int lastCstaSessionId = 0;

    private static Provider instance;

    private Asterisk asteriskServer;

    public void setAsterisk(Asterisk asterisk) {
        this.asteriskServer = asterisk;
    }

    private Provider(String countryCode, String areaCode, String systemPrefix) {
        this.countryCode = countryCode;
        this.areaCode = areaCode;
        this.systemPrefix = systemPrefix;
        sessionManager = new CSTASessionManager();
        devices = new ArrayList<Device>();
        connections = new ArrayList<Connection>();
        calls = new ArrayList<Call>();
    }

    public static Provider getInstance(String countryCode, String areaCode, String systemPrefix) {
        if(instance == null) {
            instance = new Provider(countryCode, areaCode, systemPrefix);
        }
        return instance;
    }

    public static Provider getExistingInstance() {
        return instance;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getSystemPrefix() {
        return systemPrefix;
    }

    /**
     * Search for connections participating in the Call specified by call
     * @param call the Call to search for
     * @return List of Connections participating in the call
     */
    public List<Connection> getConnectionsByCall(Call call) {
        return getConnectionsByCallId(call.getCallId());
    }

    /**
     * Search for connections participating in the Call specified by CallID
     * @param callId the CallID to search for
     * @return List of Connections participating in the call
     */
    public List<Connection> getConnectionsByCallId(String callId) {
        return connections
                .stream()
                .filter(c -> c.getCallId().equals(callId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Search for a specific callID in Provider
     * @param callId The callID to search for
     * @return The Call Object or null if no Call Object with the fiven callID was found
     */
    public Call getCallByCallId(String callId) {
        return calls.stream()
                .filter(c -> c.getCallId().equals(callId))
                .findFirst().get();
    }

    public CSTAXmlSerializable startSession(StartApplicationSession message, Channel clientChannel) {
        int sessionId = getCstaSessionId();
        sessionManager.newSession(
                new CSTASession(sessionId,
                        message.getRequestedProtocolVersion(),
                        message.getRequestedSessionDuration(),
                        clientChannel)
        );

        return new StartApplicationSessionPosResponse(
                sessionId,
                message.getRequestedProtocolVersion(),
                message.getRequestedSessionDuration());
    }

    private int getCstaSessionId() {
        return lastCstaSessionId++;
    }

    public void handleMessage(int invokeId, CSTAMessage message, Channel clientChannel) {
        CSTAXmlSerializable response = null;
        boolean disconnectClient = false;

        CSTASession session = sessionManager.findSessionForChannel(clientChannel);

        switch(message.getClass().getSimpleName()) {
            case "StartApplicationSession":
                response = startSession((StartApplicationSession)message, clientChannel);
                break;
            case "ResetApplicationSessionTimer":
                ResetApplicationSessionTimer reset = (ResetApplicationSessionTimer)message;
                response = sessionManager.resetSessionTimers(reset.getSessionID(), reset.getRequestedSessionDuration());
                break;
            case "StopApplicationSession":
                StopApplicationSession stop = (StopApplicationSession)message;
                response = sessionManager.removeSession(sessionManager.getSessionById(stop.getSessionID()));
                disconnectClient = true;
                break;
            case "GetPhysicalDeviceInformation":
                GetPhysicalDeviceInformation getPhysicalDeviceInformation = (GetPhysicalDeviceInformation)message;
                response = getPhysicalDeviceInformation(getPhysicalDeviceInformation.getDevice());
                break;
            case "MonitorStart":
                MonitorStart mStart = (MonitorStart)message;
                response = monitorStart(mStart, session);
                break;
            case "MonitorStop":
                MonitorStop mStop = (MonitorStop)message;
                response = monitorStop(mStop, session);
                break;
            case "SnapshotDevice":
                SnapshotDevice mSnapshot = (SnapshotDevice)message;
                response = snapshotDevice(mSnapshot, session);
                break;
            case "GetDoNotDisturb":
                getDoNotDisturb((GetDoNotDisturb)message, invokeId, clientChannel);
                break;
            case "SetDoNotDisturb":
                setDoNotDisturb((SetDoNotDisturb)message, invokeId, session);
                break;
            case "GetForwarding":
                getForwarding((GetForwarding)message, invokeId, clientChannel);
                break;
            case "SetForwarding":
                setForwarding((SetForwarding)message, invokeId, session);
                break;
            default:
                Log.e(TAG, "Could not handle message type " + message.getClass().getSimpleName());
                break;
        }

        if(response != null) {
            sendResponseToClient(clientChannel, invokeId, response);
        }

        if(disconnectClient) {
            Log.i(TAG, "Closing connection to Client (" + clientChannel.remoteAddress().toString() + ")");
            clientChannel.close();
        }
    }

    private void setForwarding(SetForwarding setfwd, int invokeId, CSTASession session) {
        asteriskServer.putAsteriskDatabaseValue(setfwd.getDevice().toString(), setfwd.getForwardingType().toString(), setfwd.getActivate() ? setfwd.getDN() : "");
        sendResponseToClient(session.getClientChannel(), invokeId, new SetForwardingResponse());
        MonitorPoint mp = session.getMonitorPointForDevice(setfwd.getDevice().toString());
        if(mp != null) {
            ForwardingEvent event = new ForwardingEvent(
                    mp.getCrossReferenceId(),
                    setfwd.getDevice(),
                    setfwd.getForwardingType(),
                    setfwd.getDN(),
                    setfwd.getActivate()
            );
            sendEventToClient(session.getClientChannel(), event);
        }
    }

    private void setDoNotDisturb(SetDoNotDisturb setdnd, int invokeId, CSTASession session) {
        asteriskServer.putAsteriskDatabaseValue(setdnd.getDevice().toString(), "dnd", Boolean.toString(setdnd.getOn()));
        sendResponseToClient(session.getClientChannel(), invokeId, new SetDoNotDisturbResponse());
        MonitorPoint mp = session.getMonitorPointForDevice(setdnd.getDevice().toString());
        if(mp != null) {
            DoNotDisturbEvent event = new DoNotDisturbEvent(mp.getCrossReferenceId(), setdnd.getDevice(), setdnd.getOn());
            sendEventToClient(session.getClientChannel(), event);
        }
    }

    private void sendResponseToClient(Channel clientChannel, int invokeId, CSTAXmlSerializable response) {
        CSTATcpMessage tcpResponse = new CSTATcpMessage(invokeId, CSTAXmlEncoder.getInstance().toXmlString(response));
        Log.d(TAG, "Sending Response to Client (" + clientChannel.remoteAddress().toString() + "):\n" + tcpResponse.getBody());
        clientChannel.write(tcpResponse.toByteBuf());
        clientChannel.flush();
    }

    private void sendEventToClient(Channel clientChannel, CSTAXmlSerializable event) {
        CSTATcpMessage tcpEvent = new CSTATcpMessage(CSTATcpMessage.EVENT_INVOKE_ID, CSTAXmlEncoder.getInstance().toXmlString(event));
        Log.d(TAG, "Sending Event to Client (" + clientChannel.remoteAddress().toString() + "):\n" + tcpEvent.getBody());
        clientChannel.write(tcpEvent.toByteBuf());
        clientChannel.flush();
    }

    private void getDoNotDisturb(GetDoNotDisturb getdnd, int invokeId, Channel channel) {
        asteriskServer.retrieveAsteriskDatabaseValue(getdnd.getDevice().toString(), "dnd", new Asterisk.OnAsteriskDatabaseValueRetrieved() {
            @Override
            public void onValueRetrieved(String value) {
                sendResponseToClient(channel, invokeId, new GetDoNotDisturbResponse(Boolean.parseBoolean(value)));
            }
        });
    }

    private void getForwarding(GetForwarding getfwd, int invokeId, Channel channel) {
        asteriskServer.retrieveAsteriskDatabaseValue(getfwd.getDevice().toString(), "forwardImmediate", new Asterisk.OnAsteriskDatabaseValueRetrieved() {
            @Override
            public void onValueRetrieved(String value) {
                GetForwardingResponse response = new GetForwardingResponse();
                response.addForwarding(new Forwarding(
                        Forwarding.ForwardingType.Immediate,
                        value.length() > 0,
                        value
                ));
                sendResponseToClient(channel, invokeId, response);
            }
        });
    }

    private GetPhysicalDeviceInformationResponse getPhysicalDeviceInformation(DeviceId deviceId) {
        Device d = findDeviceById(deviceId.toString());
        if(d != null) {
            return new GetPhysicalDeviceInformationResponse(d);
        }
        return null;
    }

    private MonitorStartResponse monitorStart(MonitorStart start, CSTASession session) {
        Device d = findDeviceById(start.getDeviceId());
        if(d != null) {
            MonitorPoint m = session.createMonitorPoint(d);
            return new MonitorStartResponse(m.getCrossReferenceId());
        }
        return null;
    }

    private MonitorStopResponse monitorStop(MonitorStop stop, CSTASession session) {
        MonitorPoint m = session.findMonitorPointByCrossReferenceId(stop.getMonitorCrossRefID());
        session.removeMonitorPoint(m);
        return new MonitorStopResponse();
    }

    private SnapshotDeviceResponse snapshotDevice(SnapshotDevice snapshotDevice, CSTASession session) {
        MonitorPoint m = session.getMonitorPointForDevice(snapshotDevice.getSnapshotObject());
        if(m != null) {
            return new SnapshotDeviceResponse(m.getCrossReferenceId());
        }
        return null;
    }

    public void addDevice(Device d) {
        if(findDeviceById(d.getDeviceId()) == null) {
            devices.add(d);
            Log.d(TAG, "Added new device: " + d.toString());
        } else {
            Log.w(TAG, "Duplicate deviceId " + d.getDeviceId() + " not adding device.");
        }
    }

    public void removeDevice(Device d) {
        if(findDeviceById(d.getDeviceId()) != null) {
            devices.remove(d);
        }
    }

    public Device findDeviceById(DeviceId deviceId) {
        return findDeviceById(deviceId.toString());
    }

    public Device findDeviceById(String deviceId) {
        if(devices.stream().filter(d -> d.getDeviceId().equals(deviceId)).count() > 0) {
            return devices.stream().filter(d -> d.getDeviceId().equals(deviceId)).findFirst().get();
        } else {
            return null;
        }
    }

    public void setDeviceState(String deviceId, DeviceState state) {
        Device d = findDeviceById(deviceId);
        if(d != null) {
            setDeviceState(d, state);
        }
    }

    public DeviceState getDeviceState(String deviceId) {
        Device d = findDeviceById(deviceId);
        if(d != null) {
            return getDeviceState(d);
        }
        return null;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void outOfService(Device d) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(d.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            OutOfServiceEvent e = new OutOfServiceEvent(
                    p.getValue().getCrossReferenceId(),
                    new DeviceId(d.getDeviceId())
            );
            sendEventToClient(p.getKey().getClientChannel(), e);
        }
    }


    public void backInService(Device d) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(d.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            BackInServiceEvent e = new BackInServiceEvent(
                    p.getValue().getCrossReferenceId(),
                    new DeviceId(d.getDeviceId())
            );
            sendEventToClient(p.getKey().getClientChannel(), e);
        }
    }

    public DeviceState getDeviceState(Device device) {
        return device.getState();
    }

    public void setDeviceState(Device d, DeviceState state) {
        d.setState(state);
    }

    public CSTASessionManager getSessionManager() {
        return sessionManager;
    }

    public Map<CSTASession, MonitorPoint> getMonitorPointsForDevice(String deviceId) {
        Map<CSTASession, MonitorPoint> points = new HashMap<>();

        sessionManager.getSessions().stream().filter(s -> s.getMonitorPointForDevice(deviceId) != null).forEach(
                s -> points.put(s, s.getMonitorPointForDevice(deviceId))
        );

        return points;
    }
}

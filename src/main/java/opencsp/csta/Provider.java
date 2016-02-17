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
    private int lastCallId = 0;

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
    public List<Connection> getConnectionsByCallId(int callId) {
        return connections
                .stream()
                .filter(c -> c.getCallId() == callId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Search for a specific callID in Provider
     * @param callId The callID to search for
     * @return The Call Object or null if no Call Object with the fiven callID was found
     */
    public Call getCallByCallId(int callId) {
        if(calls.stream().filter(c -> c.getCallId() == callId).count() > 0) {
            return calls.stream().filter(c -> c.getCallId() == callId).findFirst().get();
        }
        return null;
    }

    public Call findCallForConnection(Connection con) {
        if(calls.stream().filter(c -> c.getCallId() == con.getCallId()).count() > 0) {
            return calls.stream().filter(c -> c.getCallId() == con.getCallId()).findFirst().get();
        }
        return null;
    }

    public List<Connection> findConnectionsForDevice(Device d) {
        return connections.stream().filter(c -> c.getDeviceId().toString().equals(d.getDeviceId().toString())).collect(Collectors.toCollection(ArrayList::new));
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
        if(devices.stream().filter(d -> d.getDeviceId().toString().equals(deviceId)).count() > 0) {
            return devices.stream().filter(d -> d.getDeviceId().toString().equals(deviceId)).findFirst().get();
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
                    d.getDeviceId()
            );
            sendEventToClient(p.getKey().getClientChannel(), e);
        }
    }


    public void backInService(Device d) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(d.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            BackInServiceEvent e = new BackInServiceEvent(
                    p.getValue().getCrossReferenceId(),
                    d.getDeviceId()
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

    public Call associateConnections(String conA, String conB) {
        Connection cA = getConnectionByUniqueId(conA);
        if(cA != null) {
            Connection cB = getConnectionByUniqueId(conB);
            if(cB != null) {
                //Drop the callId of the second connection
                cB.setCallId(cA.getCallId());
                return addCall(cA.getCallId(), cA, cB);
            }
        }
        return null;
    }

    public Call addCall(int callId, Connection... connectionsToAdd) {
        Call c = new Call(callId);
        for(Connection con : connectionsToAdd) {
            c.addConnection(con);
        }
        calls.add(c);
        Log.d(TAG, "New Call: " + c.toString());
        return c;
    }

    public Connection getConnectionByUniqueId(String uniqueId) {
        if(connections.stream().filter(c -> c.getUniqueId().equals(uniqueId)).count() > 0) {
            return connections.stream().filter(c -> c.getUniqueId().equals(uniqueId)).findFirst().get();
        }
        return null;
    }

    public Map<CSTASession, MonitorPoint> getMonitorPointsForDevice(DeviceId deviceId) {
        return getMonitorPointsForDevice(deviceId.toString());
    }

    public Map<CSTASession, MonitorPoint> getMonitorPointsForDevice(String deviceId) {
        Map<CSTASession, MonitorPoint> points = new HashMap<>();

        sessionManager.getSessions().stream().filter(s -> s.getMonitorPointForDevice(deviceId) != null).forEach(
                s -> points.put(s, s.getMonitorPointForDevice(deviceId))
        );

        return points;
    }

    public void newConnection(DeviceId deviceId, String uniqueId) {
        Connection c = new Connection(lastCallId++, deviceId, uniqueId);
        Log.d(TAG, "Created new connection: " + c.toString());
        connections.add(c);
    }

    public void originated(Device d, Connection con, DeviceId calledDevice) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(d.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            CSTASession s = p.getKey();
            MonitorPoint mp = p.getValue();

            OriginatedEvent event = new OriginatedEvent(mp.getCrossReferenceId(), con, d.getDeviceId(), calledDevice);
            sendEventToClient(s.getClientChannel(), event);

            con.setConnectionState(ConnectionState.Connected);
        }
    }

    public void established(Device callingDevice, Device calledDevice, Device answeringDevice, Connection con) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(callingDevice.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            CSTASession s = p.getKey();
            MonitorPoint mp = p.getValue();

            EstablishedEvent event = new EstablishedEvent(
                    mp.getCrossReferenceId(),
                    con,
                    calledDevice.getDeviceId(),
                    callingDevice.getDeviceId(),
                    calledDevice.getDeviceId(),
                    null);
            sendEventToClient(s.getClientChannel(), event);
            con.setConnectionState(ConnectionState.Connected);
        }
    }

    public void delivered(Device callingDevice, Device calledDevice, Connection con) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(callingDevice.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            CSTASession s = p.getKey();
            MonitorPoint mp = p.getValue();

            DeliveredEvent event = new DeliveredEvent(
                mp.getCrossReferenceId(),
                con,
                calledDevice.getDeviceId(),
                callingDevice.getDeviceId(),
                calledDevice.getDeviceId(),
                null
            );
            sendEventToClient(s.getClientChannel(), event);
            con.setConnectionState(ConnectionState.Connected);
        }
    }

    public void connectionCleared(Device device, Device clearingDevice, Connection con) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(device.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            CSTASession s = p.getKey();
            MonitorPoint mp = p.getValue();

            ConnectionClearedEvent event = new ConnectionClearedEvent(
                mp.getCrossReferenceId(),
                con,
                clearingDevice.getDeviceId()
            );
            sendEventToClient(s.getClientChannel(), event);
            con.setConnectionState(ConnectionState.Null);
        }
    }

    public void held(Device holdingDevice, Connection heldConnection) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(holdingDevice.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            CSTASession s = p.getKey();
            MonitorPoint mp = p.getValue();

            HeldEvent event = new HeldEvent(
                mp.getCrossReferenceId(),
                heldConnection,
                holdingDevice.getDeviceId()
            );
            sendEventToClient(s.getClientChannel(), event);
            heldConnection.setConnectionState(ConnectionState.Hold);
        }
    }

    public void retrieved(Device retrievingDevice, Connection retrievedConnection) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(retrievingDevice.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            CSTASession s = p.getKey();
            MonitorPoint mp = p.getValue();

            RetrievedEvent event = new RetrievedEvent(
                    mp.getCrossReferenceId(),
                    retrievedConnection,
                    retrievingDevice.getDeviceId()
            );
            sendEventToClient(s.getClientChannel(), event);
            retrievedConnection.setConnectionState(ConnectionState.Connected);
        }
    }

    public void removeCall(Call callToBeRemoved) {
        Log.d(TAG, "Removing Call: " + callToBeRemoved);
        calls.remove(callToBeRemoved);
    }

    public void removeConnection(Connection conntectionToBeRemoved) {
        Log.d(TAG, "Removing Connection: " + conntectionToBeRemoved);
        connections.remove(conntectionToBeRemoved);
    }
}

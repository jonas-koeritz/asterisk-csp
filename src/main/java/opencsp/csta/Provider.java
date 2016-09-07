package opencsp.csta;

import io.netty.channel.Channel;
import opencsp.Log;
import opencsp.asterisk.Asterisk;
import opencsp.csta.messages.*;
import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlEncoder;
import opencsp.csta.xml.CSTAXmlSerializable;
import opencsp.csta.tcp.CSTATcpMessage;
import opencsp.devices.SIPPhone;
import opencsp.uacontroller.UAController;
import opencsp.uacontroller.unify.UaCSTAController;
import opencsp.util.ConfigurationProvider;

import java.util.*;
import java.util.stream.Collectors;

public class Provider {
    private static final String TAG = "Provider";

    private List<Device> devices;
    private List<Connection> connections;
    private List<Call> calls;
    private CSTASessionManager sessionManager;
    private ConfigurationProvider config;

    private String countryCode;
    private String areaCode;
    private String systemPrefix;
    private String localIp;

    private int lastCstaSessionId = 0;
    private int lastCallId = 0;

    private static Provider instance;

    private Asterisk asteriskServer;

    /**
     * UAControllers assigned to String deviceIds.
     */
    private HashMap<String,UAController> uaControllers;

    public void setAsterisk(Asterisk asterisk) {
        this.asteriskServer = asterisk;
    }

    private Provider(ConfigurationProvider config) {
        this.countryCode = config.getConfigurationValue("country_code");
        this.areaCode = config.getConfigurationValue("area_code");
        this.systemPrefix = config.getConfigurationValue("system_prefix");

        this.config = config;
        sessionManager = new CSTASessionManager();
        devices = new ArrayList<Device>();
        connections = new ArrayList<Connection>();
        calls = new ArrayList<Call>();
        uaControllers = new HashMap<String,UAController>();
    }

    public static Provider getInstance(ConfigurationProvider config) {
        if(instance == null) {
            instance = new Provider(config);
        }
        return instance;
    }

    /**
     * Get the controller associated with the specified device.
     * @param deviceId The deviceId to look for
     * @return The UAController implementation that will control the UA
     */
    private UAController getUaControllerForDevice(String deviceId) {
        return uaControllers.get(deviceId);
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
    private List<Connection> getConnectionsByCallId(int callId) {
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
        if(calls.stream().filter(c -> c.getCallId() == callId).findFirst().isPresent()) {
            return calls.stream().filter(c -> c.getCallId() == callId).findFirst().get();
        }
        return null;
    }

    public Call findCallForConnection(Connection con) {
        if(calls.stream().filter(c -> c.getCallId() == con.getCallId()).findFirst().isPresent()) {
            return calls.stream().filter(c -> c.getCallId() == con.getCallId()).findFirst().get();
        }
        return null;
    }

    public List<Connection> findConnectionsForDevice(Device d) {
        return connections.stream().filter(c -> c.getDeviceId().toString().equals(d.getDeviceId().toString())).collect(Collectors.toList());
    }

    public void transferred(Connection c1, Connection c2) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(c1.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            Call call2 = getCallByCallId(c2.getCallId());
            for(Connection c : call2.getConnections()) {
                //Not me
                if(!c.getDeviceId().toString().equals(c2.getDeviceId().toString())) {
                    c2 = c;
                }
            }
            TransferedEvent e = new TransferedEvent(
                    p.getValue().getCrossReferenceId(),
                    c1,
                    c2.getPresentation() != null ? new DeviceId(c2.getPresentation()) : c2.getDeviceId());
            sendEventToClient(p.getKey().getClientChannel(), e);
        }
        Device d = findDeviceById(c1.getDeviceId());

        DeviceId self = c1.getDeviceId();
        Call primaryCall = getCallByCallId(c1.getCallId());
        Call secondaryCall = getCallByCallId(c2.getCallId());

        Iterator<Connection> primaryCallConnections = primaryCall.getConnections().iterator();
        while(primaryCallConnections.hasNext()) {
            Connection c = primaryCallConnections.next();
            if(c.getDeviceId().toString().equals(self.toString())) {
                connectionCleared(d, d, c);
                primaryCallConnections.remove();
            }
        }

        Iterator<Connection> secondaryCallConnections = secondaryCall.getConnections().iterator();
        while(secondaryCallConnections.hasNext()) {
            Connection c = secondaryCallConnections.next();
            if(c.getDeviceId().toString().equals(self.toString())) {
                connectionCleared(d, d, c);
                secondaryCallConnections.remove();
            } else {
                primaryCall.addConnection(c);
                c.setCallId(primaryCall.getCallId());
                secondaryCallConnections.remove();
            }
        }
        removeCall(secondaryCall);
    }



    private CSTAXmlSerializable startSession(StartApplicationSession message, Channel clientChannel) {
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
            case "MakeCall":
                MakeCall mMakeCall = (MakeCall)message;
                response = makeCall(mMakeCall);
                break;
            case "ClearConnection":
                ClearConnection mClearConnection = (ClearConnection)message;
                response = clearConnection(mClearConnection);
                break;
            case "HoldCall":
                HoldCall mHoldCall = (HoldCall)message;
                response = holdCall(mHoldCall);
                break;
            case "RetrieveCall":
                RetrieveCall mRetrieveCall = (RetrieveCall)message;
                response = retrieveCall(mRetrieveCall);
                break;
            case "ConsultationCall":
                ConsultationCall mConsultationCall = (ConsultationCall)message;
                response = consultationCall(mConsultationCall);
                break;
            case "AnswerCall":
                AnswerCall mAnswerCall = (AnswerCall)message;
                response = answerCall(mAnswerCall);
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


    private MakeCallResponse makeCall(MakeCall makeCall) {
        Connection c = newConnection(makeCall.getCallingDevice(), "");
        c.setConnectionState(ConnectionState.Initiated);
        addCall(c.getCallId(), c);
        UAController ua = getUaControllerForDevice(makeCall.getCallingDevice().toString());
        if(ua != null) {
            Log.d(TAG, "UAController.makeCall()");
            ua.makeCall(makeCall.getCalledDirectoryNumber().toString());
        } else {
            Log.d(TAG, "No UAController available for deviceID=" + makeCall.getCallingDevice().toString());
        }
        return new MakeCallResponse(c);
    }

    private ConsultationCallResponse consultationCall(ConsultationCall consultationCall) {
        Connection c = newConnection(consultationCall.getExistingCall().getDeviceId(), "");
        c.setConnectionState(ConnectionState.Initiated);
        addCall(c.getCallId(), c);

        UAController ua = getUaControllerForDevice(consultationCall.getExistingCall().getDeviceId().toString());
        if(ua != null) {
            Log.d(TAG, "UAController.consultationCall()");
            ua.consultationCall(consultationCall.getConsultedDevice().toString());
        } else {
            Log.d(TAG, "No UAController available for deviceID=" + consultationCall.getExistingCall().getDeviceId().toString());
        }
        return new ConsultationCallResponse(c);
    }

    private AnswerCallResponse answerCall(AnswerCall answerCall) {
        Device device = findDeviceById(answerCall.getCallToBeAnswered().getDeviceId());

        UAController ua = getUaControllerForDevice(device.getDeviceId().toString());
        if(ua != null) {
            Log.d(TAG, "UAController.answerCall()");
            ua.answerCall();
        } else {
            Log.d(TAG, "No UAController available for deviceID=" + device.getDeviceId().toString());
        }
        return new AnswerCallResponse();
    }

    private ClearConnectionResponse clearConnection(ClearConnection clearConnection) {
        Device device = findDeviceById(clearConnection.getConnectionToBeCleared().getDeviceId());
        Call callToBeCleared = findCallForConnection(clearConnection.getConnectionToBeCleared());
        if(callToBeCleared != null) {
            Iterator<Connection> clearedConnections = callToBeCleared.getConnections().iterator();
            while (clearedConnections.hasNext()) {
                Connection con = clearedConnections.next();
                connectionCleared(
                        findDeviceById(con.getDeviceId()),
                        device,
                        con
                );
                clearedConnections.remove();
                removeConnection(con);
            }
            removeCall(callToBeCleared);
        }
        UAController ua = getUaControllerForDevice(device.getDeviceId().toString());
        if(ua != null) {
            Log.d(TAG, "UAController.clearConnection()");
            ua.clearConnection();
        } else {
            Log.d(TAG, "No UAController available for deviceID=" + device.getDeviceId().toString());
        }
        return new ClearConnectionResponse();
    }

    private HoldCallResponse holdCall(HoldCall holdCall) {
        Device device = findDeviceById(holdCall.getCallToBeHeld().getDeviceId());

        UAController ua = getUaControllerForDevice(device.getDeviceId().toString());
        //If an UAController is available, let the UA decide how to handle this request (Unify OpenStage Phones will decide on their own which call to hold)
        if(ua != null) {
            Log.d(TAG, "UAController.holdCall()");
            ua.holdCall();
        } else {
            //Notify Provider of request (Will generate CSTA Events)
            held(device, holdCall.getCallToBeHeld());
            Log.d(TAG, "No UAController available for deviceID=" + device.getDeviceId().toString());
        }
        return new HoldCallResponse();
    }

    private RetrieveCallResponse retrieveCall(RetrieveCall retrieveCall) {
        Device device = findDeviceById(retrieveCall.getCallToBeRetrieved().getDeviceId());

        //If an UAController is available, let the UA decide how to handle this request (Unify OpenStage Phones will decide on their own which call to retrieve)
        UAController ua = getUaControllerForDevice(device.getDeviceId().toString());
        if(ua != null) {
            Log.d(TAG, "UAController.retrieveCall()");
            ua.retrieveCall();
        } else {
            //Notify Provider of request (Will generate CSTA Events)
            retrieved(device, retrieveCall.getCallToBeRetrieved());
            Log.d(TAG, "No UAController available for deviceID=" + device.getDeviceId().toString());
        }
        return new RetrieveCallResponse();
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
            if(d.getClass().equals(SIPPhone.class)) {
                SIPPhone phone = (SIPPhone)d;
                String deviceType = config.getConfigurationValue(d.getDeviceId().toString());
                switch(deviceType) {
                    case UaCSTAController.TYPE:
                        uaControllers.put(d.getDeviceId().toString(), new UaCSTAController(phone, config));
                        break;
                    default:
                        Log.d(TAG, "No appropriate UAController for device " + d.getDeviceId().toString());
                        break;
                }
            }
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
        if(devices.stream().filter(d -> d.getDeviceId().toString().equals(deviceId)).findFirst().isPresent()) {
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


    public List<Call> getCalls() {
        return calls;
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
                Log.d(TAG, "Associating Connections: " + cA + " and " + cB);
                //Drop the callId of the second connection
                cB.setCallId(cA.getCallId());
                Call existingCall = getCallByCallId(cA.getCallId());
                if(existingCall == null) {
                    return addCall(cA.getCallId(), cA, cB);
                } else {
                    Iterator<Connection> connections = existingCall.getConnections().iterator();
                    while(connections.hasNext()) {
                        Connection con = connections.next();
                        if(con.getConnectionState().equals(ConnectionState.Null)) {
                            connections.remove();
                            existingCall.setLastRedirectionDevice(con.getDeviceId());
                            Log.d(TAG, "Removing Connection " + con + " from Call " + existingCall);
                        }
                    }
                    existingCall.addConnection(cB);
                    Log.d(TAG, "Added connection to Call: " + existingCall);
                }
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
        if(connections.stream().filter(c -> c.getUniqueId().equals(uniqueId)).findFirst().isPresent()) {
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

    public Connection newConnection(DeviceId deviceId, String uniqueId) {
        //If this is a station, find any half open calls for this device
        Device device = findDeviceById(deviceId);
        if(device.getCategory() == DeviceCategory.Station) {
            List<Connection> availableConnections = findConnectionsForDevice(device);
            Connection halfOpenConnection = null;
            if(availableConnections.stream().filter(c -> c.getUniqueId().equals("")).findFirst().isPresent()) {
                halfOpenConnection = availableConnections.stream().filter(c -> c.getUniqueId().equals("")).findFirst().get();
            }

            if(halfOpenConnection != null) {
                halfOpenConnection.setUniqueId(uniqueId);
                Log.d(TAG, "Associated connection to uniqueId " + uniqueId);
                return halfOpenConnection;
            } else {
                Connection c = new Connection(lastCallId++, deviceId, uniqueId);
                Log.d(TAG, "Created new connection: " + c.toString());
                connections.add(c);
                return c;
            }
        } else {
            Connection c = new Connection(lastCallId++, deviceId, uniqueId);
            Log.d(TAG, "Created new connection: " + c.toString());
            connections.add(c);
            return c;
        }
    }



    public void originated(Device d, Connection con, DeviceId calledDevice, String calledDevicePresentation) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(d.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            CSTASession s = p.getKey();
            MonitorPoint mp = p.getValue();

            OriginatedEvent event = new OriginatedEvent(
                    mp.getCrossReferenceId(),
                    con,
                    d.getDeviceId(),
                    calledDevicePresentation != null ? new DeviceId(calledDevicePresentation) : calledDevice
            );
            sendEventToClient(s.getClientChannel(), event);

            con.setConnectionState(ConnectionState.Connected);
        }
    }

    public void established(Device callingDevice, Device calledDevice, Device answeringDevice, Connection con, String callerPresentation, String calleePresentation, String answeringDevicePresentation) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(answeringDevice.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            CSTASession s = p.getKey();
            MonitorPoint mp = p.getValue();

            EstablishedEvent event = new EstablishedEvent(
                    mp.getCrossReferenceId(),
                    con,
                    answeringDevicePresentation != null ? new DeviceId(answeringDevicePresentation) : answeringDevice.getDeviceId(),
                    callerPresentation != null ? new DeviceId(callerPresentation) : callingDevice.getDeviceId(),
                    calleePresentation != null ? new DeviceId(calleePresentation) : calledDevice.getDeviceId(),
                    null);
            sendEventToClient(s.getClientChannel(), event);
            con.setConnectionState(ConnectionState.Connected);
        }

        points = getMonitorPointsForDevice(callingDevice.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            CSTASession s = p.getKey();
            MonitorPoint mp = p.getValue();

            EstablishedEvent event = new EstablishedEvent(
                    mp.getCrossReferenceId(),
                    con,
                    answeringDevicePresentation != null ? new DeviceId(answeringDevicePresentation) : answeringDevice.getDeviceId(),
                    callerPresentation != null ? new DeviceId(callerPresentation) : callingDevice.getDeviceId(),
                    calleePresentation != null ? new DeviceId(calleePresentation) : calledDevice.getDeviceId(),
                    null);
            sendEventToClient(s.getClientChannel(), event);
            con.setConnectionState(ConnectionState.Connected);
        }
    }

    public void queued(Device d, Connection con, Device queue) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(d.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            QueuedEvent event = new QueuedEvent(
                    p.getValue().getCrossReferenceId(),
                    con,
                    queue);

            sendEventToClient(p.getKey().getClientChannel(), event);
            con.setConnectionState(ConnectionState.Queued);
        }
    }

    public void delivered(Device callingDevice, Device calledDevice, Connection con, String callerPresentation, String calleePresentation) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(con.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            CSTASession s = p.getKey();
            MonitorPoint mp = p.getValue();

            if(con.getConnectionState().equals(ConnectionState.Alerting)) {
                DeliveredEvent event = new DeliveredEvent(
                        mp.getCrossReferenceId(),
                        con,
                        calleePresentation != null ? new DeviceId(calleePresentation) : calledDevice.getDeviceId(),
                        callerPresentation != null ? new DeviceId(callerPresentation) : callingDevice.getDeviceId(),
                        calleePresentation != null ? new DeviceId(calleePresentation) : calledDevice.getDeviceId(),
                        null,
                        ConnectionState.Alerting
                );
                sendEventToClient(s.getClientChannel(), event);
                con.setConnectionState(ConnectionState.Alerting);
            } else {
                DeliveredEvent event = new DeliveredEvent(
                        mp.getCrossReferenceId(),
                        con,
                        calleePresentation != null ? new DeviceId(calleePresentation) : calledDevice.getDeviceId(),
                        callerPresentation != null ? new DeviceId(callerPresentation) : callingDevice.getDeviceId(),
                        calleePresentation != null ? new DeviceId(calleePresentation) : calledDevice.getDeviceId(),
                        null
                );
                sendEventToClient(s.getClientChannel(), event);
                con.setConnectionState(ConnectionState.Connected);
            }
        }
    }

    public void offered(Device callingDevice, Device calledDevice, Connection con, String callerPresentation, String calleePresentation) {
        Map<CSTASession, MonitorPoint> points = getMonitorPointsForDevice(calledDevice.getDeviceId());
        for(Map.Entry<CSTASession, MonitorPoint> p : points.entrySet()) {
            CSTASession s = p.getKey();
            MonitorPoint mp = p.getValue();

            OfferedEvent event = new OfferedEvent(
                    mp.getCrossReferenceId(),
                    con,
                    calleePresentation != null ? new DeviceId(calleePresentation) : calledDevice.getDeviceId(),
                    callerPresentation != null ? new DeviceId(callerPresentation) : callingDevice.getDeviceId(),
                    calleePresentation != null ? new DeviceId(calleePresentation) : calledDevice.getDeviceId(),
                    null
            );
            sendEventToClient(s.getClientChannel(), event);
            con.setConnectionState(ConnectionState.Alerting);
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

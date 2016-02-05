package opencsp.csta;

import io.netty.channel.Channel;
import opencsp.Log;
import opencsp.csta.messages.ResetApplicationSessionTimer;
import opencsp.csta.messages.StartApplicationSession;
import opencsp.csta.messages.StartApplicationSessionPosResponse;
import opencsp.csta.messages.StopApplicationSession;
import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlEncoder;
import opencsp.csta.xml.CSTAXmlSerializable;
import opencsp.tcp.CSTATcpMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Provider {
    private static final String TAG = "Provider";

    private List<Device> devices;
    private List<Connection> connections;
    private List<Call> calls;
    private List<MonitorPoint> monitorPoints;
    private CSTASessionManager sessionManager;


    private int lastCstaSessionId = 0;

    public Provider() {
        sessionManager = new CSTASessionManager();
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
            default:
                Log.e(TAG, "Could not handle message type " + message.getClass().getSimpleName());
                break;
        }

        if(response != null) {
            CSTATcpMessage tcpResponse = new CSTATcpMessage(invokeId, CSTAXmlEncoder.getInstance().toXmlString(response));
            Log.d(TAG, "Sending Response to Client (" + clientChannel.remoteAddress().toString() + "):\n" + tcpResponse.getBody());
            clientChannel.write(tcpResponse.toByteBuf());
            clientChannel.flush();
        }

        if(disconnectClient) {
            Log.i(TAG, "Closing connection to Client (" + clientChannel.remoteAddress().toString() + ")");
            clientChannel.close();
        }
    }

    public CSTASessionManager getSessionManager() {
        return sessionManager;
    }
}

package opencsp.csta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Provider {
    private List<Device> devices;
    private List<Connection> connections;
    private List<Call> calls;

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
}

package opencsp.csta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Provider {
    private List<Device> devices;
    private List<Connection> connections;
    private List<Call> calls;

    public List<Connection> getConnectionsByCall(Call call) {
        return connections
                .stream()
                .filter(c -> c.getCallId().equals(call.getCallId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Connection> getConnectionsByCallId(String callId) {
        return connections
                .stream()
                .filter(c -> c.getCallId().equals(callId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Call getCallByCallId(String callId) {
        return calls.stream()
                .filter(c -> c.getCallId().equals(callId))
                .findFirst().get();
    }
}

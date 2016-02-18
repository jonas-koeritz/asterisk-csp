package opencsp.csta.types;

import java.util.ArrayList;
import java.util.List;

public class Call {
    private int callId;
    private List<Connection> connections;

    private DeviceId lastRedirectionDevice;

    public void setLastRedirectionDevice(DeviceId lastRedirectionDevice) {
        this.lastRedirectionDevice = lastRedirectionDevice;
    }


    public DeviceId getLastRedirectionDevice() {
        return lastRedirectionDevice;
    }

    public int getCallId() {
        return callId;
    }

    public Call(int callId) {
        this.callId = callId;
        this.connections = new ArrayList<Connection>();
    }

    public void addConnection(Connection c) {
        connections.add(c);
    }


    public List<Connection> getConnections() {
        return connections;
    }

    public boolean hasConnection(Connection c) {
        return connections.stream().filter(con -> con.getUniqueId().equals(c.getUniqueId())).count() > 0;
    }

    public String toString() {
        return "[Call: callId=" + callId + " #connections=" + connections.size() + "]";
    }
}

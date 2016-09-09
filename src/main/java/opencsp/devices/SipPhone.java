package opencsp.devices;


import opencsp.csta.types.Device;
import opencsp.csta.types.DeviceCategory;

public class SIPPhone extends Device {
    private String ipAddress;
    private int port = 5060;
    private String technology;

    public SIPPhone(String deviceId, String technology, String ipAddress, int port) {
        this.deviceId = deviceId;
        this.category = DeviceCategory.Station;
        this.ipAddress = ipAddress;
        this.port = port;
        this.technology = technology;
    }

    public String toString() {
        return "[SIPPhone deviceId=" + deviceId + ", state=" + state + ", ipAddress=" + ipAddress + ", port=" + port + "]";
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public String getTechnology() {
        return technology;
    }
}

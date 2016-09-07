package opencsp.devices;


import opencsp.csta.types.Device;
import opencsp.csta.types.DeviceCategory;

public class SIPPhone extends Device {
    private String ipAddress;
    private int port = 5060;

    public SIPPhone(String deviceId, String ipAddress, int port) {
        this.deviceId = deviceId;
        this.category = DeviceCategory.Station;
        this.ipAddress = ipAddress;
        this.port = port;
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
}

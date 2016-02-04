package opencsp.devices;


import opencsp.csta.types.Device;
import opencsp.csta.types.DeviceCategory;

public class SIPTrunk extends Device {
    public SIPTrunk(int maxChannels) {
        this.category = DeviceCategory.NetworkInterface;
    }
}

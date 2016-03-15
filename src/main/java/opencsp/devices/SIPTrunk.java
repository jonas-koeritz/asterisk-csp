package opencsp.devices;


import opencsp.csta.types.Device;
import opencsp.csta.types.DeviceCategory;

public class SIPTrunk extends Device {
    int maxChannels = 0;

    public SIPTrunk(int maxChannels) {
        this.category = DeviceCategory.NetworkInterface;
        this.maxChannels = maxChannels;
    }
}

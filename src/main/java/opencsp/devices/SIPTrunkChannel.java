package opencsp.devices;

import opencsp.csta.types.Device;
import opencsp.csta.types.DeviceCategory;

public class SIPTrunkChannel extends Device {
    public SIPTrunkChannel() {
        this.category = DeviceCategory.NetworkInterface;
    }
}

package opencsp.devices;


import opencsp.csta.types.Device;
import opencsp.csta.types.DeviceCategory;

public class SIPPhone extends Device {
    public SIPPhone() {
        this.category = DeviceCategory.Station;
    }
}

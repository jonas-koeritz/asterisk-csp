package opencsp.devices;

import opencsp.csta.types.CSTAEvent;
import opencsp.csta.types.Device;
import opencsp.csta.types.DeviceCategory;

import java.util.ArrayList;

public class Queue extends Device {
    public Queue() {
        this.category = DeviceCategory.Acd;
    }

    public Queue(String deviceId) {
        this.deviceId = deviceId;
        pendingEvents = new ArrayList<CSTAEvent>();
        this.category = DeviceCategory.Acd;
    }
}

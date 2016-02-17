package opencsp.csta.types;

import java.util.ArrayList;
import java.util.List;

public class Device {
    protected DeviceCategory category = DeviceCategory.Station;
    protected String deviceId = "";
    protected DeviceState state;

    private List<CSTAEvent> pendingEvents;



    public DeviceId getDeviceId() {
        return new DeviceId(deviceId);
    }

    public Device() {
        pendingEvents = new ArrayList<CSTAEvent>();
    }



    public Device(String deviceId) {
        this.deviceId = deviceId;
        pendingEvents = new ArrayList<CSTAEvent>();
    }

    public DeviceCategory getCategory() {
        return category;
    }

    public Device setState(DeviceState newState) {
        this.state = newState;
        return this;
    }

    public List<CSTAEvent> getPendingEvents() {
        return pendingEvents;
    }

    public void addPendingEvent(CSTAEvent event) {
        pendingEvents.add(event);
    }



    public DeviceState getState() {
        return state;
    }

    public Device setCategory(DeviceCategory category) {
        this.category = category;
        return this;
    }

    public String toString() {
        return "[Device deviceId=" + deviceId + ", category=" + category + ", state=" + state + "]";
    }
}

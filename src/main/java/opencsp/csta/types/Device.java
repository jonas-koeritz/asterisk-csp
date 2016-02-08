package opencsp.csta.types;

public class Device {
    protected DeviceCategory category = DeviceCategory.Station;
    protected String deviceId = "";
    protected DeviceState state;

    public String getDeviceId() {
        return deviceId;
    }

    public Device() {

    }

    public Device(String deviceId) {
        this.deviceId = deviceId;
    }

    public DeviceCategory getCategory() {
        return category;
    }

    public Device setState(DeviceState newState) {
        this.state = newState;
        return this;
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

package opencsp.csta.types;

import opencsp.util.IStateMachine;
import opencsp.util.State;

import java.util.ArrayList;
import java.util.List;

public class Device implements IStateMachine {
    protected DeviceCategory category = DeviceCategory.Station;
    protected String deviceId = "";
    protected DeviceState state;

    protected List<CSTAEvent> pendingEvents;

    public DeviceId getDeviceId() {
        return new DeviceId(deviceId);
    }

    public Device() {
        pendingEvents = new ArrayList<>();
    }


    public Device(String deviceId) {
        this.deviceId = deviceId;
        pendingEvents = new ArrayList<>();
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

    @Override
    public State getCurrentState() {
        return new State(state.toString());
    }

    @Override
    public boolean transitionPossible(State from, State to) {
        return false;
    }

    @Override
    public List<State> getAllStates() {
        List<State> states = new ArrayList<>();
        for(DeviceState s : DeviceState.values())
        {
            states.add(s.getState());
        }
        return states;
    }
}

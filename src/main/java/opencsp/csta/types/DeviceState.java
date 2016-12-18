package opencsp.csta.types;

import opencsp.util.State;

public enum DeviceState {
    Unknown(new State("UNKNOWN")),
    Idle(new State("NOT_INUSE")),
    InUse(new State("INUSE")),
    Busy(new State("BUSY")),
    Invalid(new State("INVALID")),
    Unavailable(new State("UNAVAILABLE")),
    Ringing(new State("RINGING")),
    RingInUse(new State("RINGINUSE")),
    Ring(new State("RING")),
    OnHold(new State("ONHOLD"));

    private final State state;

    public State getState()
    {
        return state;
    }

    DeviceState(State s) {
        state = s;
    }

    public boolean equals(State otherState) {
        return otherState != null && state.getName().equals(otherState);
    }

    public String toString() {
        return this.state.getName();
    }

    public static DeviceState fromString(String deviceState) {
        deviceState = deviceState.toUpperCase();
        switch(deviceState) {
            case "UNKNOWN":
                return Unknown;
            case "NOT_INUSE":
                return Idle;
            case "INUSE":
            case "UP":
                return InUse;
            case "BUSY":
                return Busy;
            case "INVALID":
                return Invalid;
            case "UNAVAILBLE":
                return Unavailable;
            case "RINGING":
                return Ringing;
            case "RING":
                return Ring;
            case "RINGINUSE":
                return RingInUse;
            case "ONHOLD":
                return OnHold;
        }
        return Unknown;
    }
}

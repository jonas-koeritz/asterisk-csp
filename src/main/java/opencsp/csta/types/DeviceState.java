package opencsp.csta.types;

public enum DeviceState {
    Unknown("UNKNOWN"),
    Idle("NOT_INUSE"),
    InUse("INUSE"),
    Busy("BUSY"),
    Invalid("INVALID"),
    Unavailable("UNAVAILABLE"),
    Ringing("RINGING"),
    RingInUse("RINGINUSE"),
    Ring("RING"),
    OnHold("ONHOLD");

    private final String state;

    DeviceState(String s) {
        state = s;
    }

    public boolean equals(String otherState) {
        return otherState != null && state.equals(otherState);
    }

    public String toString() {
        return this.state;
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

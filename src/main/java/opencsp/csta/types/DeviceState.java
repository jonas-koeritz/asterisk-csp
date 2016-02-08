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
}

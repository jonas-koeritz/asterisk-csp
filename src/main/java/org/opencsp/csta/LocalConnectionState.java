package org.opencsp.csta;


public enum LocalConnectionState {
    Null("null"),
    Initiated("initiated"),
    Alerting("alerting"),
    Connected("connected"),
    Hold("hold"),
    Queued("queued"),
    Fail("fail");

    private final String localConnectionState;

    LocalConnectionState(String s) {
        localConnectionState = s;
    }

    public boolean equals(String otherLocalConnectionState) {
        return otherLocalConnectionState != null && localConnectionState.equals(otherLocalConnectionState);
    }

    public String toString() {
        return this.localConnectionState;
    }
}

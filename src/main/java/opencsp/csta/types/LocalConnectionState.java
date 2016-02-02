package opencsp.csta.types;


import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public enum LocalConnectionState implements CSTAXmlSerializable {
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

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.setTextContent(localConnectionState);
        return e;
    }
}

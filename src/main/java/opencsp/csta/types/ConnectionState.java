package opencsp.csta.types;


import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public enum ConnectionState implements CSTAXmlSerializable {
    Null("null"),
    Initiated("initiated"),
    Alerting("alerting"),
    Connected("connected"),
    Hold("hold"),
    Queued("queued"),
    Fail("fail");

    private final String connectionState;

    ConnectionState(String s) {
        connectionState = s;
    }

    public boolean equals(String otherConnectionState) {
        return otherConnectionState != null && connectionState.equals(otherConnectionState);
    }

    public String toString() {
        return this.connectionState;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.setTextContent(connectionState);
        return e;
    }
}

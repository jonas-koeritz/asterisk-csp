package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResetApplicationSessionTimer extends CSTARequest implements CSTAXmlSerializable {
    private int sessionID;
    private ProtocolVersion requestedProtocolVersion;
    private int requestedSessionDuration;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element id = doc.createElement("sessionID");
        id.setTextContent(Integer.toString(sessionID));
        e.appendChild(id);

        Element d = doc.createElement("requestedSessionDuration");
        d.setTextContent(Integer.toString(requestedSessionDuration));
        e.appendChild(d);
        return e;
    }

    public ResetApplicationSessionTimer(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);

        if(xml.getElementsByTagName("sessionID").getLength() > 0) {
            this.sessionID = Integer.parseInt(xml.getElementsByTagName("sessionID").item(0).getTextContent());
        }
        if(xml.getElementsByTagName("requestedSessionDuration").getLength() > 0) {
            this.requestedSessionDuration = Integer.parseInt(xml.getElementsByTagName("requestedSessionDuration").item(0).getTextContent());
        }
    }

    public int getSessionID() {
        return sessionID;
    }

    public int getRequestedSessionDuration() {
        return requestedSessionDuration;
    }
}

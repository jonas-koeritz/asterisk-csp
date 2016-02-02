package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResetApplicationSessionTimer extends CSTARequest implements CSTAXmlSerializable {
    private String sessionID;
    private ProtocolVersion requestedProtocolVersion;
    private int requestedSessionDuration;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element id = doc.createElement("sessionID");
        id.setTextContent(sessionID);
        e.appendChild(id);

        Element d = doc.createElement("requestedSessionDuration");
        d.setTextContent(Integer.toString(requestedSessionDuration));
        e.appendChild(d);
        return e;
    }
}

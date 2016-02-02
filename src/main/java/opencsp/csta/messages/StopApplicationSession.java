package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StopApplicationSession extends CSTARequest implements CSTAXmlSerializable {
    private String sessionID;
    private String sessionEndReason;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element id = doc.createElement("sessionID");
        id.setTextContent(sessionID);
        e.appendChild(id);

        Element endReason = doc.createElement("sessionEndReason");
        Element definedEndReason = doc.createElement("definedEndReason");
        definedEndReason.setTextContent(sessionEndReason);
        endReason.appendChild(definedEndReason);

        e.appendChild(endReason);
        return e;
    }
}

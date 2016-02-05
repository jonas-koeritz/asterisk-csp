package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StopApplicationSession extends CSTARequest implements CSTAXmlSerializable {
    private int sessionID;
    private String sessionEndReason;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element id = doc.createElement("sessionID");
        id.setTextContent(Integer.toString(sessionID));
        e.appendChild(id);

        Element endReason = doc.createElement("sessionEndReason");
        Element definedEndReason = doc.createElement("definedEndReason");
        definedEndReason.setTextContent(sessionEndReason);
        endReason.appendChild(definedEndReason);

        e.appendChild(endReason);
        return e;
    }

    public StopApplicationSession(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);

        if(xml.getElementsByTagName("sessionID").getLength() > 0) {
            this.sessionID = Integer.parseInt(xml.getElementsByTagName("sessionID").item(0).getTextContent());
        }
        if(xml.getElementsByTagName("definedEndReason").getLength() > 0) {
            this.sessionEndReason = xml.getElementsByTagName("definedEndReason").item(0).getTextContent();
        }
    }

    public int getSessionID() {
        return sessionID;
    }
}

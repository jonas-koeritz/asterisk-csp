package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class StartApplicationSessionPosResponse extends CSTAResponse implements CSTAXmlSerializable {
    private String sessionID;
    private ProtocolVersion actualProtocolVersion;
    private int actualSessionDuration;

    public StartApplicationSessionPosResponse(String sessionID, ProtocolVersion actualProtocolVersion, int actualSessionDuration) {
        this.sessionID = sessionID;
        this.actualProtocolVersion = actualProtocolVersion;
        this.actualSessionDuration = actualSessionDuration;
    }

    public StartApplicationSessionPosResponse(String sessionID) {
        this.sessionID = sessionID;
        this.actualProtocolVersion = new ProtocolVersion();
        this.actualSessionDuration = 150;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element s = doc.createElement("sessionID");
        s.setTextContent(sessionID);
        e.appendChild(s);
        e.appendChild(actualProtocolVersion.toXmlElement(doc, "actualProtocolVersion"));
        Element d = doc.createElement("actualSessionDuration");
        d.setTextContent(Integer.toString(actualSessionDuration));
        e.appendChild(d);
        return e;
    }
}

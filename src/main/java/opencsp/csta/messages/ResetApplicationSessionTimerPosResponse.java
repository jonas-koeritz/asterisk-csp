package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ResetApplicationSessionTimerPosResponse extends CSTAResponse implements CSTAXmlSerializable {
    private int actualSessionDuration;

    public ResetApplicationSessionTimerPosResponse(int actualSessionDuration) {
        this.actualSessionDuration = actualSessionDuration;
    }

    public ResetApplicationSessionTimerPosResponse() {
        this.actualSessionDuration = 150;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element d = doc.createElement("actualSessionDuration");
        d.setTextContent(Integer.toString(actualSessionDuration));
        e.appendChild(d);
        return e;
    }
}

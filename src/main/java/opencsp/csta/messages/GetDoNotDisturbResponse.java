package opencsp.csta.messages;

import opencsp.csta.types.CSTAResponse;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GetDoNotDisturbResponse extends CSTAResponse implements CSTAXmlSerializable {
    private boolean doNotDisturbOn;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element on = doc.createElement("doNotDisturbOn");
        on.setTextContent(Boolean.toString(doNotDisturbOn));
        e.appendChild(on);
        return e;
    }

    public GetDoNotDisturbResponse(boolean on) {
        this.doNotDisturbOn = on;
    }
}

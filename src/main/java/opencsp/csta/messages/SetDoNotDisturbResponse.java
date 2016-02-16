package opencsp.csta.messages;

import opencsp.csta.types.CSTAResponse;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SetDoNotDisturbResponse extends CSTAResponse implements CSTAXmlSerializable {
    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        return e;
    }
}

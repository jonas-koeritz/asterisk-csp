package opencsp.csta.messages;

import opencsp.csta.types.CSTAResponse;
import opencsp.csta.types.MonitorPoint;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MonitorStopResponse extends CSTAResponse implements CSTAXmlSerializable {

    public Element toXmlElement(Document doc, String tagName) {
        return doc.createElement(tagName);
    }

    public MonitorStopResponse() {

    }
}

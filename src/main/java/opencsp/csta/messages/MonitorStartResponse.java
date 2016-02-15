package opencsp.csta.messages;

import opencsp.csta.types.CSTAResponse;
import opencsp.csta.types.CrossReferenceId;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MonitorStartResponse extends CSTAResponse implements CSTAXmlSerializable {
    CrossReferenceId crossReferenceId;


    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(crossReferenceId.toXmlElement(doc, "monitorCrossRefID"));
        return e;
    }

    public MonitorStartResponse(CrossReferenceId crossReferenceId) {
        this.crossReferenceId = crossReferenceId;
    }
}

package opencsp.csta.messages;

import opencsp.csta.types.CSTAResponse;
import opencsp.csta.types.CrossReferenceId;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SnapshotDeviceResponse extends CSTAResponse implements CSTAXmlSerializable {
    private CrossReferenceId serviceCrossRefID;

    public SnapshotDeviceResponse(CrossReferenceId id) {
        this.serviceCrossRefID = id;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(serviceCrossRefID.toXmlElement(doc, "serviceCrossRefID"));
        return e;
    }
}

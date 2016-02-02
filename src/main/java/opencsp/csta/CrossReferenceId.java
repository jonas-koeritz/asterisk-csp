package opencsp.csta;

import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class CrossReferenceId implements CSTAXmlSerializable {
    private int crossReferenceId;

    public CrossReferenceId(int id) {
        this.crossReferenceId = id;
    }

    public String toString() {
        return Integer.toString(crossReferenceId);
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.setTextContent(Integer.toString(crossReferenceId));
        return e;
    }

    public Element toXmlElement(Document doc) {
        return toXmlElement(doc, "monitorCrossRefID");
    }
}

package opencsp.csta.types;

import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ApplicationInfo implements CSTAXmlSerializable {
    private String applicationID;


    public ApplicationInfo(String applicationID) {
        this.applicationID = applicationID;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element id = doc.createElement("applicationID");
        id.setTextContent(applicationID);
        e.appendChild(id);
        return e;
    }

    public Element toXmlElement(Document doc) {
        return toXmlElement(doc, "applicationInfo");
    }
}

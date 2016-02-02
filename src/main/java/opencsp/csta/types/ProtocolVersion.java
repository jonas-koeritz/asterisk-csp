package opencsp.csta.types;

import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ProtocolVersion implements CSTAXmlSerializable {
    private String protocolVersion = " http://www.ecma-international.org/standards/ecma-323/csta/ed6";

    public ProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public ProtocolVersion() {

    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element v = doc.createElement("protocolVersion");
        v.setTextContent(protocolVersion);
        e.appendChild(v);
        return e;
    }
}

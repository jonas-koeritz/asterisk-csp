package opencsp.csta.types;

import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ProtocolVersion implements CSTAXmlSerializable {
    public static final String ECMA_323_ED3 = "http://www.ecma-international.org/standards/ecma-323/csta/ed3";
    public static final String ECMA_323_ED4 = "http://www.ecma-international.org/standards/ecma-323/csta/ed4";
    public static final String ECMA_323_ED6 = "http://www.ecma-international.org/standards/ecma-323/csta/ed6";

    private String protocolVersion = ECMA_323_ED6;


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

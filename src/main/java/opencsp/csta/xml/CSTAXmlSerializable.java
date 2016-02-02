package opencsp.csta.xml;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface CSTAXmlSerializable {
    Element toXmlElement(Document document, String tagName);
}

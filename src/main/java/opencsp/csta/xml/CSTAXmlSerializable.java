package opencsp.csta.xml;


import opencsp.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

public interface CSTAXmlSerializable {
    static final String TAG = "CSTAXmlSerializable";

    Element toXmlElement(Document document, String tagName);
    default Element toXmlElement(Document document) {
        return toXmlElement(document, getClass().getSimpleName());
    }
}

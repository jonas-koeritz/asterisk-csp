package opencsp.csta.messages.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;


public class CSTAXmlEncoder {
    private static CSTAXmlEncoder instance = null;
    private static Transformer transformer;

    public static CSTAXmlEncoder getInstance() {
        if(instance == null) {
            try {
                instance = new CSTAXmlEncoder();
                transformer = TransformerFactory.newInstance().newTransformer();

                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return instance;
    }

    private CSTAXmlEncoder() {

    }

    public String toXmlString(Document doc, Element e) {
        doc.setXmlStandalone(true);
        doc.appendChild(e);
        StringWriter writer = new StringWriter();
        try {
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

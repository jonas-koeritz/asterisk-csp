package opencsp.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import opencsp.csta.types.Connection;
import opencsp.csta.types.CrossReferenceId;
import opencsp.csta.types.DeviceId;
import opencsp.csta.messages.ServiceInitiatedEvent;
import opencsp.csta.xml.CSTAXmlEncoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringReader;

public class XmlGeneration extends TestCase {
    public XmlGeneration() {
        super();
    }

    public void testCSTAXmlGeneration() {

        boolean error = false;

        ServiceInitiatedEvent serviceInitiatedEvent = new ServiceInitiatedEvent(
                new CrossReferenceId(0),
                new Connection(12345, new DeviceId("212700"), "", "Test/12345"),
                new DeviceId("212700")
        );

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element e = serviceInitiatedEvent.toXmlElement(doc);
            String xml = CSTAXmlEncoder.getInstance().toXmlString(doc, e);
            System.out.println(xml);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document parsedDocument = builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception ex) {
            ex.printStackTrace();
            error = true;
        }

        assertFalse(error);
    }

    public static void main(String[] args) {
        TestRunner.run(XmlGeneration.class);
    }
}

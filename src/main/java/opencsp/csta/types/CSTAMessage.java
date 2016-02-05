package opencsp.csta.types;

import opencsp.Log;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class CSTAMessage {
    private static final String TAG = "CSTAMessage";

    protected Document documentFromXmlString(String xmlString) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return documentBuilder.parse(new InputSource(new StringReader(xmlString)));

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return null;
    }
}

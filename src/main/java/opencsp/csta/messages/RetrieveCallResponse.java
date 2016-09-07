package opencsp.csta.messages;

import opencsp.csta.types.CSTAResponse;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RetrieveCallResponse extends CSTAResponse implements CSTAXmlSerializable {
    public static final String TAG = "RetrieveCallResponse";

    public Element toXmlElement(Document doc, String tagName) {
        return doc.createElement(tagName);
    }

    public RetrieveCallResponse() {

    }
}

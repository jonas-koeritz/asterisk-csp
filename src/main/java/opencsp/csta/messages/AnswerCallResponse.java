package opencsp.csta.messages;

import opencsp.csta.types.CSTAResponse;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AnswerCallResponse extends CSTAResponse implements CSTAXmlSerializable {
    public static final String TAG = "AnswerCallResponse";

    public Element toXmlElement(Document doc, String tagName) {
        return doc.createElement(tagName);
    }

    public AnswerCallResponse() {

    }
}

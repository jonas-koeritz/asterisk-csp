package opencsp.csta.messages;

import opencsp.csta.types.CSTAResponse;
import opencsp.csta.types.Connection;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConsultationCallResponse extends CSTAResponse implements CSTAXmlSerializable {
    public static final String TAG = "ConsultationCallResponse";

    private Connection initiatedCall;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(initiatedCall.toXmlElement(doc, "initiatedCall"));
        return e;
    }

    public ConsultationCallResponse(Connection initiatedCall) {
        this.initiatedCall = initiatedCall;
    }
}

package opencsp.csta.messages;

import opencsp.csta.types.CSTAResponse;
import opencsp.csta.types.Call;
import opencsp.csta.types.Connection;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MakeCallResponse extends CSTAResponse implements CSTAXmlSerializable {
    public static final String TAG = "MakeCallResponse";

    private Connection callingDevice;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(callingDevice.toXmlElement(doc, "callingDevice"));
        return e;
    }

    public MakeCallResponse(Connection callingDevice) {
        this.callingDevice = callingDevice;
    }
}

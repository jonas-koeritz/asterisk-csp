package opencsp.csta;

import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Connection implements CSTAXmlSerializable {
    String callId;
    DeviceId deviceId;

    public Connection(String callId, DeviceId deviceId) {
        this.callId = callId;
        this.deviceId = deviceId;
    }

    public Connection(String callId) {
        this(callId, null);
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element cid = doc.createElement("callID");
        cid.setTextContent(this.callId);
        e.appendChild(cid);
        if(deviceId != null) {
            e.appendChild(deviceId.toXmlElement(doc, "deviceID"));
        }
        return e;
    }

    public String getCallId() {
        return callId;
    }
}

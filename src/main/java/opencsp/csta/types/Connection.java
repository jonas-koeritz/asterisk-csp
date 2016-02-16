package opencsp.csta.types;

import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Connection implements CSTAXmlSerializable {
    int callId;
    DeviceId deviceId;
    String uniqueId;

    public Connection(int callId, DeviceId deviceId, String uniqueId) {
        this.callId = callId;
        this.deviceId = deviceId;
        this.uniqueId = uniqueId;
    }


    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element cid = doc.createElement("callID");
        cid.setTextContent(Integer.toString(this.callId));
        e.appendChild(cid);
        if(deviceId != null) {
            e.appendChild(deviceId.toXmlElement(doc, "deviceID"));
        }
        return e;
    }

    public int getCallId() {
        return callId;
    }

    public String toString() {
        return "[Connection: deviceId=" + deviceId.toString() + ", callId=" + callId + ", uniqueId=" + uniqueId + "]";
    }
}

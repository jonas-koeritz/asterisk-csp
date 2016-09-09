package opencsp.csta.types;

import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Connection implements CSTAXmlSerializable {
    int callId;
    DeviceId deviceId;
    String uniqueId;
    ConnectionState state = ConnectionState.Null;
    private String presentation = "";
    private String channel;

    public Connection(int callId, DeviceId deviceId, String uniqueId, String channel) {
        this.callId = callId;
        this.deviceId = deviceId;
        this.uniqueId = uniqueId;
        this.presentation = "";
        this.channel = channel;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getPresentation() {
        return presentation;
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }

    public void setConnectionState(ConnectionState state) {
        this.state = state;
    }

    public ConnectionState getConnectionState() {
        return state;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element cid = doc.createElement("callID");
        cid.setTextContent(Integer.toString(this.callId));
        e.appendChild(cid);
        if(deviceId != null) {
            if(presentation.equals("")) {
                e.appendChild(deviceId.toXmlElement(doc, "deviceID"));
            } else {
                Element pres = doc.createElement("deviceID");
                Element ident = doc.createElement("deviceIdentifier");
                ident.setAttribute("typeOfNumber", "dialingNumber");
                ident.setTextContent(presentation);
                pres.appendChild(ident);
                e.appendChild(pres);
            }
        }
        return e;
    }

    public int getCallId() {
        return callId;
    }

    public String toString() {
        return "[Connection: deviceId=" + deviceId.toString() + ", callId=" + callId + ", uniqueId=" + uniqueId + "]";
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}

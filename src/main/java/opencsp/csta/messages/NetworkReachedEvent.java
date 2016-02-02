package opencsp.csta.messages;

import opencsp.csta.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NetworkReachedEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private Connection outboundConnection;
    private DeviceId networkInterfaceUsed;
    private DeviceId callingDevice;
    private DeviceId calledDevice;
    private DeviceId lastRedirectionDevice;
    private EventCause cause;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(outboundConnection.toXmlElement(doc, "outboundConnection"));
        e.appendChild(networkInterfaceUsed.toXmlElement(doc, "networkInterfaceUsed"));
        e.appendChild(callingDevice.toXmlElement(doc, "callingDevice"));
        e.appendChild(calledDevice.toXmlElement(doc, "calledDevice"));
        if(lastRedirectionDevice != null) {
            e.appendChild(lastRedirectionDevice.toXmlElement(doc, "lastRedirectionDevice"));
        } else {
            e.appendChild(doc.createElement("lastRedirectionDevice").appendChild(doc.createElement("notRequired")));
        }
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }
}


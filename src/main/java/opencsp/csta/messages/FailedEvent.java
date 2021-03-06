package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FailedEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private Connection failedConnection;
    private DeviceId failingDevice;
    private DeviceId callingDevice;
    private DeviceId calledDevice;
    private DeviceId lastRedirectionDevice;
    private EventCause cause;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(failedConnection.toXmlElement(doc, "failedConnection"));
        e.appendChild(failingDevice.toXmlElement(doc, "failingDevice"));
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


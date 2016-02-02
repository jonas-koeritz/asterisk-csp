package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DivertedEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private Connection connection;
    private DeviceId divertingDevice;
    private DeviceId newDestination;
    private DeviceId lastRedirectionDevice;
    private EventCause cause;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(divertingDevice.toXmlElement(doc, "divertingDevice"));
        e.appendChild(newDestination.toXmlElement(doc, "newDestination"));
        if(lastRedirectionDevice != null) {
            e.appendChild(lastRedirectionDevice.toXmlElement(doc, "lastRedirectionDevice"));
        } else {
            e.appendChild(doc.createElement("lastRedirectionDevice").appendChild(doc.createElement("notRequired")));
        }
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }
}


package opencsp.csta.messages;

import opencsp.csta.CSTAEvent;
import opencsp.csta.CrossReferenceId;
import opencsp.csta.DeviceId;
import opencsp.csta.EventCause;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OutOfServiceEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private DeviceId subjectDeviceId;
    private EventCause cause;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(subjectDeviceId.toXmlElement(doc, "device"));
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }
}

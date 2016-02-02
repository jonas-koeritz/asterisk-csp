package opencsp.csta.messages;

import opencsp.csta.types.CSTAEvent;
import opencsp.csta.types.CrossReferenceId;
import opencsp.csta.types.DeviceId;
import opencsp.csta.types.EventCause;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BackInServiceEvent extends CSTAEvent implements CSTAXmlSerializable {
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

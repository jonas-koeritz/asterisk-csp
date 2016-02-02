package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class HeldEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private Connection heldConnection;
    private DeviceId holdingDevice;
    private EventCause cause;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(heldConnection.toXmlElement(doc, "heldConnection"));
        e.appendChild(holdingDevice.toXmlElement(doc, "holdingDevice"));
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }
}


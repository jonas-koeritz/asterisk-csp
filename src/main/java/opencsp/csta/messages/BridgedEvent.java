package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BridgedEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private Connection bridgedConnection;
    private DeviceId bridgedAppearance;
    private EventCause cause;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(bridgedConnection.toXmlElement(doc, "bridgedConnection"));
        e.appendChild(bridgedAppearance.toXmlElement(doc, "bridgedAppearance"));
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }
}


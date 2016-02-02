package opencsp.csta.messages;

import opencsp.csta.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DigitsDialedEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private Connection dialingConnection;
    private DeviceId dialingDevice;
    private DeviceId dialingSequence;
    private EventCause cause;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(dialingConnection.toXmlElement(doc, "dialingConnection"));
        e.appendChild(dialingDevice.toXmlElement(doc, "dialingDevice"));
        e.appendChild(dialingSequence.toXmlElement(doc, "dialingSequence"));
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }
}


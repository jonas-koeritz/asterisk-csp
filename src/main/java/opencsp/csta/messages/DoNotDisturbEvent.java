package opencsp.csta.messages;

import opencsp.csta.types.CSTAEvent;
import opencsp.csta.types.CrossReferenceId;
import opencsp.csta.types.DeviceId;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DoNotDisturbEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private DeviceId device;
    private boolean on;

    public DoNotDisturbEvent(CrossReferenceId crossReferenceId, DeviceId device, boolean on) {
        this.monitorCrossRefID = crossReferenceId;
        this.device = device;
        this.on = on;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc, "monitorCrossRefID"));
        e.appendChild(device.toXmlElement(doc, "device"));
        Element dndOn = doc.createElement("doNotDisturbOn");
        dndOn.setTextContent(Boolean.toString(on));
        e.appendChild(dndOn);
        return e;
    }
}

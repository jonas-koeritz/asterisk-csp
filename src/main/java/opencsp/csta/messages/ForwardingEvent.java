package opencsp.csta.messages;

import opencsp.csta.types.CSTAEvent;
import opencsp.csta.types.CrossReferenceId;
import opencsp.csta.types.DeviceId;
import opencsp.csta.types.Forwarding;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ForwardingEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private DeviceId device;
    private Forwarding.ForwardingType forwardingType;
    private String forwardTo;
    private boolean forwardStatus;

    public ForwardingEvent(CrossReferenceId crossReferenceId, DeviceId device, Forwarding.ForwardingType type, String forwardTo, boolean forwardStatus) {
        this.monitorCrossRefID = crossReferenceId;
        this.device = device;
        this.forwardingType = type;
        this.forwardTo = forwardTo;
        this.forwardStatus = forwardStatus;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc, "monitorCrossRefID"));
        e.appendChild(device.toXmlElement(doc, "device"));
        e.appendChild(forwardingType.toXmlElement(doc, "forwardingType"));
        Element status = doc.createElement("forwardStatus");
        status.setTextContent(Boolean.toString(forwardStatus));
        e.appendChild(status);
        Element fwdTo = doc.createElement("forwardTo");
        fwdTo.setAttribute("typeOfNumber", "dialingNumber");
        fwdTo.setTextContent(forwardTo);
        e.appendChild(fwdTo);
        return e;
    }
}

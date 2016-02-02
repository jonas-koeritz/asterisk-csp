package opencsp.csta.messages;

import opencsp.csta.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class ConferencedEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private Connection primaryOldCall;
    private Connection secondaryOldCall;
    private DeviceId conferencingDevice;
    private DeviceId addedParty;
    private List<Connection> conferenceConnections;
    private EventCause cause;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(primaryOldCall.toXmlElement(doc, "primaryOldCall"));
        e.appendChild(secondaryOldCall.toXmlElement(doc, "secondaryOldCall"));
        e.appendChild(conferencingDevice.toXmlElement(doc, "conferencingDevice"));
        e.appendChild(addedParty.toXmlElement(doc, "addedParty"));
        Element connectionList = doc.createElement("conferenceConnections");
        conferenceConnections.forEach(
                c -> connectionList.appendChild(c.toXmlElement(doc, "connectionListItem"))
        );
        e.appendChild(connectionList);
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }

    public Element toXmlElement(Document doc) {
        return toXmlElement(doc, "ConferencedEvent");
    }
}


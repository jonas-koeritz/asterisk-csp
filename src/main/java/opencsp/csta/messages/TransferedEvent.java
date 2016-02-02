package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class TransferedEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private Connection primaryOldCall;
    private DeviceId transferringDevice;
    private DeviceId transferredToDevice;
    private List<Connection> transferredConnections;
    private EventCause cause;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(primaryOldCall.toXmlElement(doc, "primaryOldCall"));
        e.appendChild(transferringDevice.toXmlElement(doc, "transferringDevice"));
        e.appendChild(transferredToDevice.toXmlElement(doc, "transferredToDevice"));
        Element connectionList = doc.createElement("transferredConnections");
        transferredConnections.forEach(
                c -> connectionList.appendChild(c.toXmlElement(doc, "connectionListItem"))
        );
        e.appendChild(connectionList);
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }
}


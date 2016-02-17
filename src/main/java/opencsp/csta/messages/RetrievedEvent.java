package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RetrievedEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private Connection retrievedConnection;
    private DeviceId retrievingDevice;
    private EventCause cause = EventCause.Normal;

    public RetrievedEvent(CrossReferenceId monitorCrossRefID, Connection retrievedConnection, DeviceId retrievingDevice) {
        this.monitorCrossRefID = monitorCrossRefID;
        this.retrievedConnection = retrievedConnection;
        this.retrievingDevice = retrievingDevice;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(retrievedConnection.toXmlElement(doc, "retrievedConnection"));
        e.appendChild(retrievingDevice.toXmlElement(doc, "retrievingDevice"));
        Element localConnectionInfo = doc.createElement("localConnectionInfo");
        localConnectionInfo.setTextContent("connected");
        e.appendChild(localConnectionInfo);
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }
}


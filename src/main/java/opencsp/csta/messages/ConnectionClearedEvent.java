package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConnectionClearedEvent extends CSTAEvent implements CSTAXmlSerializable {
    CrossReferenceId monitorCrossRefID;

    Connection droppedConnection;

    DeviceId releasingDevice;

    LocalConnectionState localConnectionInfo = LocalConnectionState.Null;

    public ConnectionClearedEvent(CrossReferenceId monitorCrossRefID, Connection droppedConnection, DeviceId releasingDevice) {
        this(monitorCrossRefID, droppedConnection, releasingDevice, EventCause.NormalClearing);
    }

    public ConnectionClearedEvent(CrossReferenceId monitorCrossRefID, Connection droppedConnection, DeviceId releasingDevice, EventCause cause) {
        this.monitorCrossRefID = monitorCrossRefID;
        this.droppedConnection = droppedConnection;
        this.releasingDevice = releasingDevice;
        this.cause = cause;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(droppedConnection.toXmlElement(doc, "droppedConnection"));
        e.appendChild(releasingDevice.toXmlElement(doc, "releasingDevice"));
        e.appendChild(localConnectionInfo.toXmlElement(doc, "localConnectionInfo"));
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }
}

package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OriginatedEvent extends CSTAEvent implements CSTAXmlSerializable {
    CrossReferenceId monitorCrossRefID;
    Connection originatedConnection;
    DeviceId callingDevice;
    DeviceId calledDevice;

    ConnectionState localConnectionInfo = ConnectionState.Connected;

    public OriginatedEvent(CrossReferenceId monitorCrossRefID, Connection originatedConnection, DeviceId callingDevice, DeviceId calledDevice) {
        this(monitorCrossRefID, originatedConnection, callingDevice, calledDevice, EventCause.NewCall);
    }

    public OriginatedEvent(CrossReferenceId monitorCrossRefID, Connection originatedConnection, DeviceId callingDevice, DeviceId calledDevice, EventCause cause) {
        this.monitorCrossRefID = monitorCrossRefID;
        this.originatedConnection = originatedConnection;
        this.callingDevice = callingDevice;
        this.calledDevice = calledDevice;
        this.cause = cause;
    }


    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(originatedConnection.toXmlElement(doc, "originatedConnection"));
        e.appendChild(callingDevice.toXmlElement(doc, "callingDevice"));
        e.appendChild(calledDevice.toXmlElement(doc, "calledDevice"));
        e.appendChild(localConnectionInfo.toXmlElement(doc, "localConnectionInfo"));
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }
}

package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DeliveredEvent extends CSTAEvent implements CSTAXmlSerializable {
    CrossReferenceId monitorCrossRefID;

    Connection connection;

    DeviceId alertingDevice;

    DeviceId callingDevice;

    DeviceId calledDevice;

    DeviceId lastRedirectionDevice;

    LocalConnectionState localConnectionInfo = LocalConnectionState.Connected;

    public DeliveredEvent(CrossReferenceId monitorCrossRefID, Connection connection, DeviceId alertingDevice, DeviceId callingDevice, DeviceId calledDevice, DeviceId lastRedirectionDevice) {
        this(monitorCrossRefID, connection, alertingDevice, callingDevice, calledDevice, lastRedirectionDevice, EventCause.NewCall);
    }

    public DeliveredEvent(CrossReferenceId monitorCrossRefID, Connection connection, DeviceId alertingDevice, DeviceId callingDevice, DeviceId calledDevice, DeviceId lastRedirectionDevice, EventCause cause) {
        this.monitorCrossRefID = monitorCrossRefID;
        this.connection = connection;
        this.alertingDevice = alertingDevice;
        this.callingDevice = callingDevice;
        this.calledDevice = calledDevice;
        this.lastRedirectionDevice = lastRedirectionDevice;
        this.cause = cause;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(connection.toXmlElement(doc, "connection"));
        e.appendChild(alertingDevice.toXmlElement(doc, "alertingDevice"));
        e.appendChild(callingDevice.toXmlElement(doc, "callingDevice"));
        e.appendChild(calledDevice.toXmlElement(doc, "calledDevice"));
        if(lastRedirectionDevice != null) {
            e.appendChild(lastRedirectionDevice.toXmlElement(doc, "lastRedirectionDevice"));
        } else {
            e.appendChild(doc.createElement("lastRedirectionDevice").appendChild(doc.createElement("notRequired")));
        }
        e.appendChild(localConnectionInfo.toXmlElement(doc, "localConnectionInfo"));
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }
}

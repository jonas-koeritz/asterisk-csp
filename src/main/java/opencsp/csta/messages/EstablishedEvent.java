package opencsp.csta.messages;

import opencsp.csta.CrossReferenceId;
import opencsp.csta.LocalConnectionState;
import opencsp.csta.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EstablishedEvent extends CSTAEvent implements CSTAXmlSerializable {
    CrossReferenceId monitorCrossRefID;

    Connection establishedConnection;

    DeviceId answeringDevice;

    DeviceId callingDevice;

    DeviceId calledDevice;

    DeviceId lastRedirectionDevice;

    LocalConnectionState localConnectionInfo = LocalConnectionState.Connected;

    public EstablishedEvent(CrossReferenceId monitorCrossRefID, Connection establishedConnection, DeviceId answeringDevice, DeviceId callingDevice, DeviceId calledDevice, DeviceId lastRedirectionDevice) {
        this(monitorCrossRefID, establishedConnection, answeringDevice, callingDevice, calledDevice, lastRedirectionDevice, EventCause.NewCall);
    }

    public EstablishedEvent(CrossReferenceId monitorCrossRefID, Connection establishedConnection, DeviceId answeringDevice, DeviceId callingDevice, DeviceId calledDevice, DeviceId lastRedirectionDevice, EventCause cause) {
        this.monitorCrossRefID = monitorCrossRefID;
        this.establishedConnection = establishedConnection;
        this.answeringDevice = answeringDevice;
        this.callingDevice = callingDevice;
        this.calledDevice = calledDevice;
        this.lastRedirectionDevice = lastRedirectionDevice;
        this.cause = cause;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(establishedConnection.toXmlElement(doc, "establishedConnection"));
        e.appendChild(answeringDevice.toXmlElement(doc, "answeringDevice"));
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

    public Element toXmlElement(Document doc) {
        return toXmlElement(doc, "EstablishedEvent");
    }
}

package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class QueuedEvent extends CSTAEvent implements CSTAXmlSerializable {
    private CrossReferenceId monitorCrossRefID;
    private Connection queuedConnection;
    private DeviceId queue;
    private DeviceId callingDevice;
    private DeviceId calledDevice;
    private DeviceId lastRedirectionDevice;
    private EventCause cause;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(queuedConnection.toXmlElement(doc, "queuedConnection"));
        e.appendChild(queue.toXmlElement(doc, "queue"));
        e.appendChild(callingDevice.toXmlElement(doc, "callingDevice"));
        e.appendChild(calledDevice.toXmlElement(doc, "calledDevice"));
        if(lastRedirectionDevice != null) {
            e.appendChild(lastRedirectionDevice.toXmlElement(doc, "lastRedirectionDevice"));
        } else {
            Element lrd = doc.createElement("lastRedirectionDevice");
            lrd.appendChild(doc.createElement("notRequired"));
            e.appendChild(lrd);
        }
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }

    public QueuedEvent(CrossReferenceId monitorCrossRefID, Connection queuedConnection, Device queue) {
        this.queue = queue.getDeviceId();
        this.callingDevice = !queuedConnection.getPresentation().equals("") ? new DeviceId(queuedConnection.getPresentation()) : queuedConnection.getDeviceId();
        this.calledDevice = queue.getDeviceId();
        this.cause = EventCause.Normal;
        this.queuedConnection = queuedConnection;
        this.monitorCrossRefID = monitorCrossRefID;
    }
}


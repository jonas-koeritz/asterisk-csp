package opencsp.csta.messages;

import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ServiceInitiatedEvent extends CSTAEvent implements CSTAXmlSerializable {
    protected CrossReferenceId monitorCrossRefID;

    protected Connection initiatedConnection;

    protected DeviceId initiatingDevice;

    protected LocalConnectionState localConnectionInfo = LocalConnectionState.Initiated;

    public ServiceInitiatedEvent(CrossReferenceId monitorCrossRefID, Connection initiatedConnection, DeviceId initiatingDevice) {
        this.monitorCrossRefID = monitorCrossRefID;
        this.initiatedConnection = initiatedConnection;
        this.initiatingDevice = initiatingDevice;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc));
        e.appendChild(initiatedConnection.toXmlElement(doc, "initiatedConnection"));
        e.appendChild(initiatingDevice.toXmlElement(doc, "initiatingDevice"));
        e.appendChild(localConnectionInfo.toXmlElement(doc, "localConnectionInfo"));
        e.appendChild(cause.toXmlElement(doc));
        return e;
    }
}

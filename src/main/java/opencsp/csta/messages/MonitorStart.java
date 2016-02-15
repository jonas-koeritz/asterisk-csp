package opencsp.csta.messages;

import opencsp.Log;
import opencsp.csta.Provider;
import opencsp.csta.types.CSTARequest;
import opencsp.csta.types.DeviceId;
import opencsp.csta.xml.CSTAXmlSerializable;
import opencsp.util.PhoneNumber;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MonitorStart extends CSTARequest implements CSTAXmlSerializable {
    private static final String TAG = "MonitorStart";

    private String monitorType = "device";
    private DeviceId deviceId;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(deviceId.toXmlElement(doc, "monitorObject"));
        Element eMonitorType = doc.createElement("monitorType");
        eMonitorType.setTextContent(monitorType);
        e.appendChild(eMonitorType);
        return e;
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }

    public MonitorStart(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);
        NodeList deviceObjects = xml.getElementsByTagName("deviceObject");
        if(deviceObjects.getLength() == 1) {
            Provider p = Provider.getExistingInstance();
            if(p != null) {
                String device = PhoneNumber.cleanup(p, deviceObjects.item(0).getTextContent());
                deviceId = new DeviceId(device);
            } else {
                Log.w(TAG, "Could no handle request, no existing provider");
            }
        } else {
            Log.e(TAG, "MonitorStart MUST contain one deviceObject.");
        }

        NodeList monitorTypeObjects = xml.getElementsByTagName("monitorType");
        if(monitorTypeObjects.getLength() == 1) {
            monitorType = monitorTypeObjects.item(0).getTextContent();
        }
    }
}

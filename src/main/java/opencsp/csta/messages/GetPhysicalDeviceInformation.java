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


public class GetPhysicalDeviceInformation extends CSTARequest implements CSTAXmlSerializable {
    private static final String TAG = "GetPhysicalDeviceInformation";

    private DeviceId device;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(device.toXmlElement(doc, "device"));
        return e;
    }

    public DeviceId getDevice() {
        return device;
    }

    public GetPhysicalDeviceInformation(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);
        NodeList deviceNodes = xml.getElementsByTagName("device");
        if(deviceNodes.getLength() == 1) {
            Provider p = Provider.getExistingInstance();
            if(p != null) {
                String deviceNumber = deviceNodes.item(0).getTextContent();
                deviceNumber = PhoneNumber.cleanup(p, deviceNumber);
                this.device = new DeviceId(deviceNumber);
            } else {
                Log.w(TAG, "Could no handle request, no existing provider");
            }
        } else {
            Log.e(TAG, "GetPhysicalDeviceInformation MUST contain a single Device ID.");
        }
    }
}

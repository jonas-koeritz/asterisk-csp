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


public class SetDoNotDisturb extends CSTARequest implements CSTAXmlSerializable {
    private boolean on;
    private DeviceId device;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(device.toXmlElement(doc, "device"));
        Element dndOn = doc.createElement("doNotDisturbOn");
        dndOn.setTextContent(Boolean.toString(on));
        e.appendChild(dndOn);
        return e;
    }

    public DeviceId getDevice() {
        return device;
    }

    public boolean getOn() {
        return on;
    }

    public SetDoNotDisturb(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);
        NodeList devices = xml.getElementsByTagName("device");

        if(devices.getLength() == 1) {
            Provider p = Provider.getExistingInstance();
            if(p != null) {
                String d = PhoneNumber.cleanup(p, devices.item(0).getTextContent());
                device = new DeviceId(d);
                on = Boolean.parseBoolean(xml.getElementsByTagName("doNotDisturbOn").item(0).getTextContent());
            } else {
                Log.w(TAG, "Could no handle request, no existing provider");
            }
        } else {
            Log.e(TAG, "SetDoNotDisturb MUST contain one device.");
        }
    }
}

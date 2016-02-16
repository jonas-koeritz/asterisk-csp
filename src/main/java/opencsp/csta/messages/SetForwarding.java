package opencsp.csta.messages;

import opencsp.Log;
import opencsp.csta.Provider;
import opencsp.csta.types.CSTARequest;
import opencsp.csta.types.DeviceId;
import opencsp.csta.types.Forwarding;
import opencsp.csta.xml.CSTAXmlSerializable;
import opencsp.util.PhoneNumber;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SetForwarding extends CSTARequest implements CSTAXmlSerializable {
    private DeviceId device;
    private Forwarding.ForwardingType forwardingType;
    private boolean activate;
    private String dn;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(device.toXmlElement(doc, "device"));

        return e;
    }

    public DeviceId getDevice() {
        return device;
    }

    public Forwarding.ForwardingType getForwardingType() {
        return forwardingType;
    }

    public boolean getActivate() {
        return activate;
    }

    public String getDN() {
        return dn;
    }

    public SetForwarding(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);
        NodeList devices = xml.getElementsByTagName("device");

        if(devices.getLength() == 1) {
            Provider p = Provider.getExistingInstance();
            if(p != null) {
                String d = PhoneNumber.cleanup(p, devices.item(0).getTextContent());
                device = new DeviceId(d);

                forwardingType = Forwarding.ForwardingType.getEnum(xml.getElementsByTagName("forwardingType").item(0).getTextContent());
                activate = Boolean.parseBoolean(xml.getElementsByTagName("activateForward").item(0).getTextContent());
                if(xml.getElementsByTagName("forwardDN").getLength() > 0) {
                    dn = xml.getElementsByTagName("forwardDN").item(0).getTextContent();
                } else {
                    dn = "";
                }
            } else {
                Log.w(TAG, "Could no handle request, no existing provider");
            }
        } else {
            Log.e(TAG, "SetForwarding MUST contain one device.");
        }
    }
}

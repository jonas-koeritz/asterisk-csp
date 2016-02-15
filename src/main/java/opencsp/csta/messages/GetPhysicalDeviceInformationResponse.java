package opencsp.csta.messages;

import opencsp.csta.types.CSTAResponse;
import opencsp.csta.types.Device;
import opencsp.csta.types.DeviceCategory;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GetPhysicalDeviceInformationResponse extends CSTAResponse implements CSTAXmlSerializable {
    private Device device;

    public GetPhysicalDeviceInformationResponse(Device device) {
        this.device = device;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element category = doc.createElement("deviceCategory");
        category.setTextContent(device.getCategory().toString());
        Element hasLogicalElement = doc.createElement("hasLogicalElement");
        hasLogicalElement.setTextContent("true");
        e.appendChild(category);
        e.appendChild(hasLogicalElement);
        return e;
    }
}

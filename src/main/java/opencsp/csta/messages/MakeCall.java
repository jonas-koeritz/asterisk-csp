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

public class MakeCall extends CSTARequest implements CSTAXmlSerializable {
    private static final String TAG = "MakeCall";

    private DeviceId callingDevice;
    private DeviceId calledDirectoryNumber;
    private String autoOriginate = "doNotPrompt";

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(callingDevice.toXmlElement(doc, "callingDevice"));
        e.appendChild(calledDirectoryNumber.toXmlElement(doc, "calledDirectoryNumber"));
        Element aa = doc.createElement("autoOriginate");
        aa.setTextContent(autoOriginate);
        e.appendChild(aa);
        return e;
    }

    public DeviceId getCallingDevice() {
        return callingDevice;
    }

    public DeviceId getCalledDirectoryNumber() {
        return calledDirectoryNumber;
    }

    public MakeCall(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);
        NodeList callingDeviceNodes = xml.getElementsByTagName("callingDevice");
        NodeList calledDirectoryNumberNodes = xml.getElementsByTagName("calledDirectoryNumber");

        if(callingDeviceNodes.getLength() == 1 && calledDirectoryNumberNodes.getLength() == 1) {
            Provider p = Provider.getExistingInstance();
            if(p != null) {
                String deviceNumber = callingDeviceNodes.item(0).getTextContent();
                deviceNumber = PhoneNumber.cleanup(p, deviceNumber);
                callingDevice = new DeviceId(deviceNumber);

                String calledNumber = calledDirectoryNumberNodes.item(0).getTextContent();
                calledNumber = PhoneNumber.cleanup(p, calledNumber);
                calledDirectoryNumber = new DeviceId(calledNumber);
            } else {
                Log.w(TAG, "Could no handle request, no existing provider");
            }
        } else {
            Log.e(TAG, "MakeCall MUST contain a single callingDevice and a single calledDirectoryNumber.");
        }
    }
}

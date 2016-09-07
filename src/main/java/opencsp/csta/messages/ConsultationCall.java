package opencsp.csta.messages;

import opencsp.Log;
import opencsp.csta.Provider;
import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import opencsp.util.PhoneNumber;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ConsultationCall extends CSTARequest implements CSTAXmlSerializable {
    private static final String TAG = "ConsultationCall";

    private Connection existingCall;
    private DeviceId consultedDevice;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(existingCall.toXmlElement(doc, "existingCall"));
        e.appendChild(consultedDevice.toXmlElement(doc, "consultedDevice"));
        return e;
    }

    public Connection getExistingCall() {
        return existingCall;
    }

    public DeviceId getConsultedDevice() {
        return consultedDevice;
    }

    public ConsultationCall(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);
        NodeList callIDNodes = xml.getElementsByTagName("callID");
        NodeList deviceIdNodes = xml.getElementsByTagName("deviceID");
        NodeList consultedDeviceNodes = xml.getElementsByTagName("consultedDevice");

        if(callIDNodes.getLength() == 1 && consultedDeviceNodes.getLength() == 1 && deviceIdNodes.getLength() == 1) {
            Provider p = Provider.getExistingInstance();
            if(p != null) {
                Call call = p.getCallByCallId(Integer.parseInt(callIDNodes.item(0).getTextContent()));
                for(Connection c : call.getConnections()) {
                    //No deviceId Specified
                    if(deviceIdNodes.item(0).getTextContent().length() == 0) {
                        //find the first station involved in this call
                        if (p.findDeviceById(c.getDeviceId()).getCategory().equals(DeviceCategory.Station)) {
                            this.existingCall = c;
                            break;
                        }
                    } else {
                        //Find the connection with the device specified by the message
                        if (c.getDeviceId().toString().equals(deviceIdNodes.item(0).getTextContent())) {
                            this.existingCall = c;
                            break;
                        }
                    }
                }
                consultedDevice = new DeviceId(consultedDeviceNodes.item(0).getTextContent());
            } else {
                Log.w(TAG, "Could no handle request, no existing provider");
            }
        } else {
            Log.e(TAG, "ConsultationCall MUST at least contain a single callID and a single consultedDevice.");
        }
    }
}

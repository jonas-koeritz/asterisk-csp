package opencsp.csta.messages;

import opencsp.Log;
import opencsp.csta.Provider;
import opencsp.csta.types.CSTARequest;
import opencsp.csta.types.Call;
import opencsp.csta.types.Connection;
import opencsp.csta.types.DeviceCategory;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ReconnectCall extends CSTARequest implements CSTAXmlSerializable {
    private static final String TAG = "ReconnectCall";

    private Connection heldCall;
    private Connection activeCall;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(activeCall.toXmlElement(doc, "activeCall"));
        e.appendChild(heldCall.toXmlElement(doc, "heldCall"));
        return e;
    }

    public Connection getActiveCall() {
        return activeCall;
    }
    public Connection getHeldCall() {
        return heldCall;
    }

    public ReconnectCall(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);
        NodeList heldCallNodes = xml.getElementsByTagName("heldCall");
        NodeList activeCallNodes = xml.getElementsByTagName("activeCall");


        if(heldCallNodes.getLength() == 1 && activeCallNodes.getLength() == 1) {
            Provider p = Provider.getExistingInstance();

            Element heldCall = (Element)heldCallNodes.item(0);
            Element activeCall = (Element)activeCallNodes.item(0);

            NodeList heldCallCallId = heldCall.getElementsByTagName("callID");
            NodeList heldCallDeviceId = heldCall.getElementsByTagName("deviceID");
            NodeList activeCallCallId = activeCall.getElementsByTagName("callID");
            NodeList activeCallDeviceId = activeCall.getElementsByTagName("deviceID");

            if(heldCallCallId.getLength() == 1 && heldCallDeviceId.getLength() == 1 &&
                    activeCallCallId.getLength() == 1 && activeCallDeviceId.getLength() == 1) {
                String heldCallId = heldCallCallId.item(0).getTextContent();
                String heldCallDevice = heldCallDeviceId.item(0).getTextContent();
                String activeCallId = activeCallCallId.item(0).getTextContent();
                String activeCallDevice = activeCallDeviceId.item(0).getTextContent();

                Call callToBeCleared = p.getCallByCallId(Integer.parseInt(activeCallId));
                for(Connection c : callToBeCleared.getConnections()) {
                    //No deviceId Specified
                    if(activeCallDevice.equals("")) {
                        //find the first station involved in this call
                        if (p.findDeviceById(c.getDeviceId()).getCategory().equals(DeviceCategory.Station)) {
                            this.activeCall = c;
                            break;
                        }
                    } else {
                        //Find the connection with the device specified by the message
                        if (c.getDeviceId().toString().equals(activeCallDevice)) {
                            this.activeCall = c;
                            break;
                        }
                    }
                }

                Call callToBeRetrieved = p.getCallByCallId(Integer.parseInt(heldCallId));
                for(Connection c : callToBeRetrieved.getConnections()) {
                    //No deviceId Specified
                    if(heldCallDevice.equals("")) {
                        //find the first station involved in this call
                        if (p.findDeviceById(c.getDeviceId()).getCategory().equals(DeviceCategory.Station)) {
                            this.heldCall = c;
                            break;
                        }
                    } else {
                        //Find the connection with the device specified by the message
                        if (c.getDeviceId().toString().equals(heldCallDevice)) {
                            this.heldCall = c;
                            break;
                        }
                    }
                }
            } else {
                Log.e(TAG, "ReconnectCall MUST contain a single heldCall and a single activeCall with callID and deviceID.");
            }
        } else {
            Log.e(TAG, "ReconnectCall MUST contain a single heldCall and a single activeCall.");
        }
    }
}

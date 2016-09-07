package opencsp.csta.messages;

import opencsp.Log;
import opencsp.csta.Provider;
import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import opencsp.util.PhoneNumber;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ClearConnection extends CSTARequest implements CSTAXmlSerializable {
    private static final String TAG = "ClearConnection";

    private Connection connectionToBeCleared;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(connectionToBeCleared.toXmlElement(doc, "connectionToBeCleared"));
        return e;
    }

    public Connection getConnectionToBeCleared() {
        return connectionToBeCleared;
    }

    public ClearConnection(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);
        NodeList connectionToBeClearedNodes = xml.getElementsByTagName("connectionToBeCleared");
        NodeList callIDNodes = xml.getElementsByTagName("callID");
        NodeList deviceIdNodes = xml.getElementsByTagName("deviceID");

        if(connectionToBeClearedNodes.getLength() == 1 && callIDNodes.getLength() == 1 && deviceIdNodes.getLength() == 1) {
            Provider p = Provider.getExistingInstance();
            if(p != null) {
                Call callToBeCleared = p.getCallByCallId(Integer.parseInt(callIDNodes.item(0).getTextContent()));
                for(Connection c : callToBeCleared.getConnections()) {
                    //No deviceId Specified
                    if(deviceIdNodes.item(0).getTextContent().length() == 0) {
                        //find the first station involved in this call
                        if (p.findDeviceById(c.getDeviceId()).getCategory().equals(DeviceCategory.Station)) {
                            this.connectionToBeCleared = c;
                            break;
                        }
                    } else {
                        //Find the connection with the device specified by the message
                        if (c.getDeviceId().toString().equals(deviceIdNodes.item(0).getTextContent())) {
                            this.connectionToBeCleared = c;
                            break;
                        }
                    }
                }
            }
        } else {
            Log.e(TAG, "ClearConnection MUST contain a single connectionToBeCleared with a single callID and a single deviceID.");
        }
    }
}

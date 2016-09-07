package opencsp.csta.messages;

import opencsp.Log;
import opencsp.csta.Provider;
import opencsp.csta.types.*;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AnswerCall extends CSTARequest implements CSTAXmlSerializable {
    private static final String TAG = "AnswerCall";

    private Connection callToBeAnswered;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(callToBeAnswered.toXmlElement(doc, "callToBeAnswered"));
        return e;
    }

    public Connection getCallToBeAnswered() {
        return callToBeAnswered;
    }

    public AnswerCall(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);
        NodeList callToBeAnsweredNodes = xml.getElementsByTagName("callToBeAnswered");
        NodeList callIDNodes = xml.getElementsByTagName("callID");
        NodeList deviceIdNodes = xml.getElementsByTagName("deviceID");

        if(callToBeAnsweredNodes.getLength() == 1 && callIDNodes.getLength() == 1 && deviceIdNodes.getLength() == 1) {
            Provider p = Provider.getExistingInstance();
            if(p != null) {
                Call callToBeAnswered = p.getCallByCallId(Integer.parseInt(callIDNodes.item(0).getTextContent()));
                for(Connection c : callToBeAnswered.getConnections()) {
                    //No deviceId Specified
                    if(deviceIdNodes.item(0).getTextContent().length() == 0) {
                        //find the first alerting connection in this call
                        if (c.getConnectionState().equals(ConnectionState.Alerting)) {
                            this.callToBeAnswered = c;
                            break;
                        }
                    } else {
                        //Find the connection with the device specified by the message
                        if (c.getDeviceId().toString().equals(deviceIdNodes.item(0).getTextContent())) {
                            this.callToBeAnswered = c;
                            break;
                        }
                    }
                }
            }
        } else {
            Log.e(TAG, "AnswerCall MUST contain a single callToBeAnswered with a single callID and a single deviceID.");
        }
    }
}

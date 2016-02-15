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

public class SnapshotDevice extends CSTARequest implements CSTAXmlSerializable {
    private static final String TAG = "SnapshotDevice";

    private DeviceId snapshotObject;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(snapshotObject.toXmlElement(doc, "snapshotObject"));
        return e;
    }

    public SnapshotDevice(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);
        NodeList snapshotObjectNodes = xml.getElementsByTagName("snapshotObject");
        if(snapshotObjectNodes.getLength() == 1) {
            Provider p = Provider.getExistingInstance();
            if(p != null) {
                String deviceNumber = snapshotObjectNodes.item(0).getTextContent();
                deviceNumber = PhoneNumber.cleanup(p, deviceNumber);
                snapshotObject = new DeviceId(deviceNumber);
            } else {
                Log.w(TAG, "Could no handle request, no existing provider");
            }
        } else {
            Log.e(TAG, "SnapshotDevice MUST contain a single snapshotObject.");
        }
    }

    public DeviceId getSnapshotObject() {
        return snapshotObject;
    }
}

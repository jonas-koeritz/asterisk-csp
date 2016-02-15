package opencsp.csta.messages;

import opencsp.Log;
import opencsp.csta.types.CSTARequest;
import opencsp.csta.types.CrossReferenceId;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MonitorStop extends CSTARequest implements CSTAXmlSerializable {
    private static final String TAG = "MonitorStop";

    CrossReferenceId monitorCrossRefID;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(monitorCrossRefID.toXmlElement(doc, "monitorCrossRefID"));
        return e;
    }

    public CrossReferenceId getMonitorCrossRefID() {
        return monitorCrossRefID;
    }

    public MonitorStop(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);
        NodeList crossReferenceIds = xml.getElementsByTagName("monitorCrossRefID");
        if(crossReferenceIds.getLength() == 1) {
            monitorCrossRefID = new CrossReferenceId(Integer.parseInt(crossReferenceIds.item(0).getTextContent()));
        } else {
            Log.e(TAG, "MonitorStop MUST contain one monitorCrossRefID.");
        }
    }
}

package opencsp.csta.messages;

import opencsp.csta.types.ApplicationInfo;
import opencsp.csta.types.CSTARequest;
import opencsp.csta.types.ProtocolVersion;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StartApplicationSession extends CSTARequest implements CSTAXmlSerializable {
    private ApplicationInfo applicationInfo;
    private ProtocolVersion requestedProtocolVersion;
    private int requestedSessionDuration;

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(applicationInfo.toXmlElement(doc, "applicationInfo"));
        e.appendChild(requestedProtocolVersion.toXmlElement(doc, "requestedProtocolVersions"));
        Element v = doc.createElement("requestedSessionDuration");
        v.setTextContent(Integer.toString(requestedSessionDuration));
        e.appendChild(v);
        return e;
    }
}

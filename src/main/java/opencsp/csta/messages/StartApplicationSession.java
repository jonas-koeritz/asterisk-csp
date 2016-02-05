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

    public StartApplicationSession(String xmlBody) {
        Document xml = documentFromXmlString(xmlBody);
        this.applicationInfo = new ApplicationInfo();
        this.requestedProtocolVersion = new ProtocolVersion();

        if(xml.getElementsByTagName("applicationID").getLength() > 0) {
            this.applicationInfo.setApplicationID(xml.getElementsByTagName("applicationID").item(0).getTextContent());
        }
        if(xml.getElementsByTagName("user").getLength() > 0) {
            this.applicationInfo.setUser(xml.getElementsByTagName("user").item(0).getTextContent());
        }
        if(xml.getElementsByTagName("password").getLength() > 0) {
            this.applicationInfo.setPassword(xml.getElementsByTagName("password").item(0).getTextContent());
        }

        if(xml.getElementsByTagName("protocolVersion").getLength() > 0) {
            this.requestedProtocolVersion.setProtocolVersion(xml.getElementsByTagName("protocolVersion").item(0).getTextContent());
        }

        if(xml.getElementsByTagName("requestedSessionDuration").getLength() > 0) {
            this.requestedSessionDuration = Integer.parseInt(xml.getElementsByTagName("requestedSessionDuration").item(0).getTextContent());
        }
    }

    public ProtocolVersion getRequestedProtocolVersion() {
        return requestedProtocolVersion;
    }

    public int getRequestedSessionDuration() {
        return requestedSessionDuration;
    }
}

package opencsp.csta.types;

import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Forwarding implements CSTAXmlSerializable {
    private ForwardingType forwardingType;
    private boolean forwardStatus;
    private String forwardDN;

    public Forwarding(ForwardingType type, boolean status, String dn) {
        this.forwardingType = type;
        this.forwardStatus = status;
        this.forwardDN = dn;
    }

    public static enum ForwardingType {
        Immediate("forwardImmediate"),
        Busy("forwardBusy"),
        NoAnswer("forwardNoAns"),
        DoNotDisturb("forwardDND"),
        BusyInternal("forwardBusyInt"),
        NoAnswerInternal("forwardNoAnsInt"),
        NoAnswerExternal("forwardNoAnsExt"),
        ImmediateInternal("forwardImmInt"),
        ImmediateExternal("forwardImmExt"),
        DoNotDisturbInternal("forwardDNDInt"),
        DoNotDisturbExternal("forwardDNDExt");

        private final String type;

        ForwardingType(String s) {
            type = s;
        }

        public boolean equalsForwardingType(String otherType) {
            return otherType != null && type.equals(otherType);
        }

        public String toString() {
            return this.type;
        }

        public Element toXmlElement(Document doc, String tagName) {
            Element e = doc.createElement(tagName);
            e.setTextContent(type);
            return e;
        }

        public Element toXmlElement(Document doc) {
            return toXmlElement(doc, "forwardingType");
        }
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.appendChild(forwardingType.toXmlElement(doc));
        Element status = doc.createElement("forwardStatus");
        status.setTextContent(Boolean.toString(forwardStatus));
        e.appendChild(status);
        Element dn = doc.createElement("forwardDN");
        dn.setTextContent(forwardDN);
        e.appendChild(dn);
        return e;
    }
}

package opencsp.csta.messages;

import opencsp.csta.types.CSTAResponse;
import opencsp.csta.types.Forwarding;
import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;


public class GetForwardingResponse extends CSTAResponse implements CSTAXmlSerializable {
    List<Forwarding> forwardings;

    public GetForwardingResponse() {
        forwardings = new ArrayList<Forwarding>();
    }

    public void addForwarding(Forwarding fwd) {
        forwardings.add(fwd);
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element list = doc.createElement("forwardingList");
        forwardings.forEach(f -> list.appendChild(f.toXmlElement(doc, "forwardListItem")));
        e.appendChild(list);
        return e;
    }

}

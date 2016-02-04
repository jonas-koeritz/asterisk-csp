package opencsp.csta.types;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public enum DeviceCategory {
    Acd("acd"),
    GroupAcd("groupACD"),
    GroupHunt("groupHunt"),
    GroupPick("groupPick"),
    GroupUser("groupUser"),
    GroupOther("groupOther"),
    NetworkInterface("networkInterface"),
    Park("park"),
    RouteingDevice("routeingDevice"),
    Station("station"),
    VoiceUnity("voiceUnit"),
    GenericIV("genericIV"),
    ListenerIV("listenerIV"),
    DtmfIV("dtmfIV"),
    PromptIV("promptIV"),
    PromptQueue("promptQueue"),
    MessageIV("messageIV"),
    Conference("conference"),
    Other("other");

    private final String category;


    DeviceCategory(String s) {
        category = s;
    }

    public boolean equals(String otherCategory) {
        return otherCategory != null && category.equals(otherCategory);
    }

    public String toString() {
        return this.category;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.setTextContent(category);
        return e;
    }

    public Element toXmlElement(Document doc) {
        return toXmlElement(doc, "deviceType");
    }
}

package opencsp.csta.types;

import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ApplicationInfo implements CSTAXmlSerializable {
    private String applicationID;
    private String user;
    private String password;


    public ApplicationInfo(String applicationID) {
        this.applicationID = applicationID;
    }

    public ApplicationInfo(String applicationID, String user, String password) {
        this.applicationID = applicationID;
        this.user = user;
        this.password = password;
    }

    public ApplicationInfo() {

    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        Element id = doc.createElement("applicationID");
        id.setTextContent(applicationID);
        Element specificInfo = doc.createElement("applicationSpecificInfo");
        Element eUser = doc.createElement("user");
        eUser.setTextContent(user);
        Element ePassword = doc.createElement("password");
        ePassword.setTextContent(password);
        specificInfo.appendChild(eUser);
        specificInfo.appendChild(ePassword);
        e.appendChild(id);
        e.appendChild(specificInfo);
        return e;
    }

    public Element toXmlElement(Document doc) {
        return toXmlElement(doc, "applicationInfo");
    }
}

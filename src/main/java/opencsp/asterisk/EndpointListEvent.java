package opencsp.asterisk;

import org.asteriskjava.manager.event.ManagerEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EndpointListEvent extends ManagerEvent {
    private String aor;
    private String objectName;
    private String deviceState;
    private String actionId;
    private String transport;
    private String objectType;
    private String contacts;
    private String auths;

    public String getIpAddress() {
        return ipAddress;
    }

    public int getIpPort() {
        return ipPort;
    }

    private String ipAddress = "";
    private int ipPort = 0;

    public String getObjectName() {
        return objectName;
    }

    public String getAuths() {
        return auths;
    }

    public String getDeviceState() {
        return deviceState;
    }

    public String getActionId() {
        return actionId;
    }

    public String getTransport() {
        return transport;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getContacts() {
        return contacts;
    }

    public String getAor() {
        return aor;
    }


    public EndpointListEvent(Object source) {
        super(source);
    }

    public void setAor(String aor) {
        this.aor = aor;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setDeviceState(String deviceState) {
        this.deviceState = deviceState;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
        Pattern hostAndPort = Pattern.compile(".*@([\\d\\.]*):(\\d*).*$");
        Matcher m = hostAndPort.matcher(contacts);
        if(m.matches()) {
            if (m.groupCount() == 2) {
                this.ipAddress = m.group(1);
                this.ipPort = Integer.parseInt(m.group(2));
            }
        }
    }

    public void setAuths(String auths) {
        this.auths = auths;
    }
}

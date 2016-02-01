package org.opencsp.csta;


import org.opencsp.csta.messages.encoding.xml.CSTAXml;

@CSTAXml(name="deviceIdentifier")
public class DeviceId {
    @CSTAXml(name="typeOfNumber")
    private String typeOfNumber = "dialingNumber";

    private String deviceIdentifier;

    public String toString() {
        return deviceIdentifier;
    }
}

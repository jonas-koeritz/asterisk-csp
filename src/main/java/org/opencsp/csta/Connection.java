package org.opencsp.csta;

import org.opencsp.csta.messages.encoding.xml.CSTAXml;

public class Connection {
    @CSTAXml(name="callID")
    private String callId;

    @CSTAXml(name="deviceID")
    DeviceId deviceId;
}

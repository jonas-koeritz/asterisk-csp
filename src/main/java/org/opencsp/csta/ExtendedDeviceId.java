package org.opencsp.csta;

import org.opencsp.csta.messages.encoding.xml.CSTAXml;

@CSTAXml(name="extendedDeviceId")
public class ExtendedDeviceId {
    @CSTAXml(name="deviceIdentifier")
    DeviceId deviceId;
}

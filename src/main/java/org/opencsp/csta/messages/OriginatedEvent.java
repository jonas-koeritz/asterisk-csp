package org.opencsp.csta.messages;

import org.opencsp.csta.*;
import org.opencsp.csta.messages.encoding.xml.CSTAXml;

@CSTAXml(name="OriginatedEvent")
public class OriginatedEvent extends CSTAEvent {
    @CSTAXml(name="monitorCrossRefID")
    CrossReferenceId monitorCrossRefID;

    @CSTAXml(name="originatedConnection")
    Connection originatedConnection;

    @CSTAXml(name="callingDevice")
    ExtendedDeviceId callingDevice;

    @CSTAXml(name="calledDevice")
    ExtendedDeviceId calledDevice;

    @CSTAXml(name="localConnectionInfo")
    LocalConnectionState localConnectionInfo = LocalConnectionState.Connected;
}

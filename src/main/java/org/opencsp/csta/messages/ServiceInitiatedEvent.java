package org.opencsp.csta.messages;

import org.opencsp.csta.*;
import org.opencsp.csta.messages.encoding.xml.CSTAXml;

@CSTAXml(name="ServiceInitiatedEvent")
public class ServiceInitiatedEvent extends CSTAEvent {
    @CSTAXml(name="monitorCrossRefID")
    CrossReferenceId monitorCrossRefID;

    @CSTAXml(name="initiatedConnection")
    Connection initiatedConnection;

    @CSTAXml(name="initiatingDevice")
    ExtendedDeviceId initiatingDevice;

    @CSTAXml(name="localConnectionInfo")
    LocalConnectionState localConnectionInfo = LocalConnectionState.Initiated;
}

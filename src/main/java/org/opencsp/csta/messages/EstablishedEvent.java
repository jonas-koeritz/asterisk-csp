package org.opencsp.csta.messages;

import org.opencsp.csta.*;
import org.opencsp.csta.messages.encoding.xml.CSTAXml;

@CSTAXml(name="EstablishedEvent")
public class EstablishedEvent extends CSTAEvent {
    @CSTAXml(name="monitorCrossRefID")
    CrossReferenceId monitorCrossRefID;

    @CSTAXml(name="answeringDevice")
    ExtendedDeviceId answeringDevice;

    @CSTAXml(name="callingDevice")
    ExtendedDeviceId callingDevice;

    @CSTAXml(name="calledDevice")
    ExtendedDeviceId calledDevice;

    @CSTAXml(name="lastRedirectionDevice", required=false)
    ExtendedDeviceId lastRedirectionDevice;

    @CSTAXml(name="localConnectionInfo")
    LocalConnectionState localConnectionInfo = LocalConnectionState.Connected;
}

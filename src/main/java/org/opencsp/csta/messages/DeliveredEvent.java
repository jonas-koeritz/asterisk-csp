package org.opencsp.csta.messages;

import org.opencsp.csta.*;
import org.opencsp.csta.messages.CSTAEvent;
import org.opencsp.csta.messages.encoding.xml.CSTAXml;

@CSTAXml(name="DeliveredEvent")
public class DeliveredEvent extends CSTAEvent {
    @CSTAXml(name="monitorCrossRefID")
    CrossReferenceId monitorCrossRefID;

    @CSTAXml(name="connection")
    Connection connection;

    @CSTAXml(name="alertingDevice")
    ExtendedDeviceId alertingDevice;

    @CSTAXml(name="callingDevice")
    ExtendedDeviceId callingDevice;

    @CSTAXml(name="calledDevice")
    ExtendedDeviceId calledDevice;

    @CSTAXml(name="lastRedirectionDevice", required=false)
    ExtendedDeviceId lastRedirectionDevice;

    @CSTAXml(name="localConnectionInfo")
    LocalConnectionState localConnectionInfo = LocalConnectionState.Connected;
}

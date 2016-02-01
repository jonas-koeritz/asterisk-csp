package org.opencsp.csta.messages;

import org.opencsp.csta.*;
import org.opencsp.csta.messages.encoding.xml.CSTAXml;

@CSTAXml(name="ConnectionClearedEvent")
public class ConnectionClearedEvent extends CSTAEvent {
    @CSTAXml(name="monitorCrossRefID")
    CrossReferenceId monitorCrossRefID;

    @CSTAXml(name="droppedConnection")
    Connection droppedConnection;

    @CSTAXml(name="releasingDevice")
    ExtendedDeviceId releasingDevice;

    @CSTAXml(name="localConnectionInfo")
    LocalConnectionState localConnectionInfo = LocalConnectionState.Null;
}

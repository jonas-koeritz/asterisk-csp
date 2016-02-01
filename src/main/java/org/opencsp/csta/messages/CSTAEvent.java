package org.opencsp.csta.messages;


import org.opencsp.csta.messages.encoding.xml.CSTAXml;

@CSTAXml(name="Event")
public class CSTAEvent {
    @CSTAXml(name="cause")
    EventCause cause;
}

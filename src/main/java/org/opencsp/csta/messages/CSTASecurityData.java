package org.opencsp.csta.messages;

/**
 * The CSTASecurityData parameter type provides information that can be used to determine if a message
 * in a sequence has been lost, the time that a message was sent, and security information that can be
 * used to provide security such as access control and authentication.
 */

public class CSTASecurityData {
    private long messageSequenceNumber;
    private long timeStamp;
    private String securityInfo;
}

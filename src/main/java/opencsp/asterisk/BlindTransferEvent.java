package opencsp.asterisk;

import org.asteriskjava.manager.event.ManagerEvent;

public class BlindTransferEvent extends ManagerEvent {
    public BlindTransferEvent(Object source) {
            super(source);
        }
    /*
    transfereelinkedid=1473264782.356,
    transfererconnectedlinenum=02853505,
    transfereechannel=PJSIP/fx5-0000009b,
    transfereeuniqueid=1473264783.357,
    transfererchannelstate=6,
    transfereechannelstate=6,
    context=internal,
    bridgetechnology=native_rtp,
    transfereeconnectedlinenum=+4941212853505,
    transfereechannelstatedesc=Up,
    transfererchannelstatedesc=Up,
    isexternal=Yes,
    transfererexten=02853505,
    bridgetype=basic,
    transfereecalleridnum=02853505,
    transferercalleridnum=+4941212853505,
    transferercontext=internal,
    extension=02853504,
    bridgeuniqueid=f8fb419b-5eab-4b39-8cf2-0c4b58aa6474,
    privilege=call,all,
    transferercalleridname=<unknown>,
    result=Success,
    event=BlindTransfer,
    transfereecalleridname=Jonas KÃ¶ritz,
    transfererlanguage=en,
    transfererchannel=PJSIP/212700-0000009a,
    bridgenumchannels=2,
    bridgecreator=<unknown>,
    transfereruniqueid=1473264782.356,
    bridgename=<unknown>,
    transfererlinkedid=1473264782.356,
    transfererpriority=3,
    transfererconnectedlinename=Jonas KÃ¶ritz,
    transfereecontext=from-trunk,
    transfereeconnectedlinename=<unknown>,
    transfereepriority=1,
    transfereelanguage=en}.
     */

    private String transfereeLinkedId;
    private String transfereeUniqueId;
    private String transfereeChannel;
    private String transfererChannel;
    private String transfererLinkedId;
    private String transfererUniqueId;

    public String getTransfereeLinkedId() {
        return transfereeLinkedId;
    }

    public void setTransfereeLinkedId(String transfereeLinkedId) {
        this.transfereeLinkedId = transfereeLinkedId;
    }

    public String getTransfereeChannel() {
        return transfereeChannel;
    }

    public void setTransfereeChannel(String transfereeChannel) {
        this.transfereeChannel = transfereeChannel;
    }

    public String getTransfereeUniqueId() {
        return transfereeUniqueId;
    }

    public void setTransfereeUniqueId(String transfereeUniqueId) {
        this.transfereeUniqueId = transfereeUniqueId;
    }

    public String getTransfererChannel() {
        return transfererChannel;
    }

    public void setTransfererChannel(String transfererChannel) {
        this.transfererChannel = transfererChannel;
    }

    public String getTransfererLinkedId() {
        return transfererLinkedId;
    }

    public void setTransfererLinkedId(String transfererLinkedId) {
        this.transfererLinkedId = transfererLinkedId;
    }

    public String getTransfererUniqueId() {
        return transfererUniqueId;
    }

    public void setTransfererUniqueId(String transfererUniqueId) {
        this.transfererUniqueId = transfererUniqueId;
    }
}

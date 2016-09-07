package opencsp.asterisk;

import org.asteriskjava.manager.event.ManagerEvent;

public class QueueCallerJoinEvent extends ManagerEvent {
    public QueueCallerJoinEvent(Object source) {
        super(source);
    }
    /*
        linkedid=1473261045.255,
        calleridname=+4941212853505,
        channel=PJSIP/fx5-00000073,
        language=en,
        privilege=agent,all,
        exten=211201,
        calleridnum=+4941212853505,
        context=from-trunk,
        event=QueueCallerJoin,
        connectedlinenum=211201,
        uniqueid=1473261045.255
        ,channelstatedesc=Up,
        count=1,
        connectedlinename=R.SH,
        priority=5,
        channelstate=6,
        position=1,
        queue=211201
         */

    public String getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(String linkedId) {
        this.linkedId = linkedId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCalleridNum() {
        return calleridNum;
    }

    public void setCalleridNum(String calleridNum) {
        this.calleridNum = calleridNum;
    }

    public String getConnectedLineNum() {
        return connectedLineNum;
    }

    public void setConnectedLineNum(String connectedLineNum) {
        this.connectedLineNum = connectedLineNum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = Integer.parseInt(count);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = Integer.parseInt(position);
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getChannelStateDesc() {
        return channelStateDesc;
    }

    public void setChannelStateDesc(String channelStateDesc) {
        this.channelStateDesc = channelStateDesc;
    }

    private String linkedId;
    private String channel;
    private String language;
    private String calleridNum;
    private String connectedLineNum;



    private String channelStateDesc;
    private int count;
    private int position;
    private String queue;


}

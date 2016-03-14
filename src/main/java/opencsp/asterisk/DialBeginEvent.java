package opencsp.asterisk;

import org.asteriskjava.manager.event.ManagerEvent;

public class DialBeginEvent extends ManagerEvent {
    public DialBeginEvent(Object source) {
        super(source);
    }

    private String destChannel;
    private String destChannelState;
    private String linkedId;
    private String destContext;
    private String callerIdName;
    private String destLinkedId;
    private String destConnectedLineName;
    private String channel;
    private String language;
    private String dialString;
    private String exten;
    private String destExten;
    private String callerIdNum;
    private String context;
    private String destUniqueId;
    private String connectedLineNum;
    private String uniqueId;
    private String channelStateDesc;
    private String destPriority;
    private String connectedLineName;
    private String priority;
    private String destCallerIdName;
    private String channelState;
    private String destConnectedLineNum;
    private String destCallerIdNum;

    public String getDestChannel() {
        return destChannel;
    }

    public void setDestChannel(String destChannel) {
        this.destChannel = destChannel;
    }

    public String getDestChannelState() {
        return destChannelState;
    }

    public void setDestChannelState(String destChannelState) {
        this.destChannelState = destChannelState;
    }

    public String getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(String linkedId) {
        this.linkedId = linkedId;
    }

    public String getDestContext() {
        return destContext;
    }

    public void setDestContext(String destContext) {
        this.destContext = destContext;
    }

    public String getCallerIdName() {
        return callerIdName;
    }

    public void setCallerIdName(String callerIdName) {
        this.callerIdName = callerIdName;
    }

    public String getDestLinkedId() {
        return destLinkedId;
    }

    public void setDestLinkedId(String destLinkedId) {
        this.destLinkedId = destLinkedId;
    }

    public String getDestConnectedLineName() {
        return destConnectedLineName;
    }

    public void setDestConnectedLineName(String destConnectedLineName) {
        this.destConnectedLineName = destConnectedLineName;
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

    public String getDialString() {
        return dialString;
    }

    public void setDialString(String dialString) {
        this.dialString = dialString;
    }

    public String getExten() {
        return exten;
    }

    public void setExten(String exten) {
        this.exten = exten;
    }

    public String getDestExten() {
        return destExten;
    }

    public void setDestExten(String destExten) {
        this.destExten = destExten;
    }

    public String getCallerIdNum() {
        return callerIdNum;
    }

    public void setCallerIdNum(String callerIdNum) {
        this.callerIdNum = callerIdNum;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDestUniqueId() {
        return destUniqueId;
    }

    public void setDestUniqueId(String destUniqueId) {
        this.destUniqueId = destUniqueId;
    }

    public String getConnectedLineNum() {
        return connectedLineNum;
    }

    public void setConnectedLineNum(String connectedLineNum) {
        this.connectedLineNum = connectedLineNum;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getChannelStateDesc() {
        return channelStateDesc;
    }

    public void setChannelStateDesc(String channelStateDesc) {
        this.channelStateDesc = channelStateDesc;
    }

    public String getDestPriority() {
        return destPriority;
    }

    public void setDestPriority(String destPriority) {
        this.destPriority = destPriority;
    }

    public String getConnectedLineName() {
        return connectedLineName;
    }

    public void setConnectedLineName(String connectedLineName) {
        this.connectedLineName = connectedLineName;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDestCallerIdName() {
        return destCallerIdName;
    }

    public void setDestCallerIdName(String destCallerIdName) {
        this.destCallerIdName = destCallerIdName;
    }

    public String getChannelState() {
        return channelState;
    }

    public void setChannelState(String channelState) {
        this.channelState = channelState;
    }

    public String getDestConnectedLineNum() {
        return destConnectedLineNum;
    }

    public void setDestConnectedLineNum(String destConnectedLineNum) {
        this.destConnectedLineNum = destConnectedLineNum;
    }

    public String getDestCallerIdNum() {
        return destCallerIdNum;
    }

    public void setDestCallerIdNum(String destCallerIdNum) {
        this.destCallerIdNum = destCallerIdNum;
    }

}

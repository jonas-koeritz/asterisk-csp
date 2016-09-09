package opencsp.asterisk;

import org.asteriskjava.manager.event.ManagerEvent;

public class MusicOnHoldStartEvent extends ManagerEvent {
    public MusicOnHoldStartEvent(Object source) {
        super(source);
    }

    private String linkedId;
    private String channel;
    private String language;
    private String exten;
    private String context;
    private String Class;
    private String uniqueId;
    private String channelStateDesc;

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

    public String getExten() {
        return exten;
    }

    public void setExten(String exten) {
        this.exten = exten;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }


    public String getMohClass() {
        return Class;
    }

    public void setClass(String aClass) {
        Class = aClass;
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
}

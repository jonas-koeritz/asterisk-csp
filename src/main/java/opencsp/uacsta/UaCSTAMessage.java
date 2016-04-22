package opencsp.uacsta;


public class UaCSTAMessage {

    private String remoteAddress;
    private int remotePort;
    private String content;

    public UaCSTAMessage(String remoteAddress, int remotePort, String content) {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.content = content;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

package opencsp.uacontroller;


public interface UAController {
    public abstract void makeCall();
    public abstract void answerCall();
    public abstract void clearConnection();
    public abstract void holdCall();
    public abstract void retrieveCall();
}

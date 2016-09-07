package opencsp.uacontroller;


public interface UAController {
    /**
     * Make the User Agent initiate a call
     * @param calledDirectoryNumber the number to dial
     */
    public abstract void makeCall(String calledDirectoryNumber);

    /**
     * Make the User Agent answer any ringing call
     */
    public abstract void answerCall();
    public abstract void clearConnection();
    public abstract void holdCall();
    public abstract void retrieveCall();

    /**
     * Make the User Agent initiate a consultation call
     * @param consultedDevice the device to consult
     */
    public abstract void consultationCall(String consultedDevice);
}

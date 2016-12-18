package opencsp.uacontroller;


import opencsp.csta.types.Connection;

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

    /**
     * Clear the currently active connection
     * @param connectionToBeCleared the connection to be cleared (if possible to choose)
     */
    public abstract void clearConnection(Connection connectionToBeCleared);

    /**
     * Put the currently active call on hold
     */
    public abstract void holdCall();

    /**
     * Retrieve a held call
     */
    public abstract void retrieveCall();

    /**
     * Make the User Agent initiate a consultation call
     * @param consultedDevice the device to consult
     */
    public abstract void consultationCall(String consultedDevice);
}

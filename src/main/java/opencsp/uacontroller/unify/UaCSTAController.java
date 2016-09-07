package opencsp.uacontroller.unify;

import opencsp.Log;
import opencsp.devices.SIPPhone;
import opencsp.uacontroller.UAController;

public class UaCSTAController implements UAController {
    public static final String TAG = "UaCSTAController";
    public static final String TYPE = "unify";

    private String ipAddress;
    private int port;

    public UaCSTAController(SIPPhone phone) {
        Log.d(TAG, "Creating new UaCSTAController for phone " + phone.toString());
        ipAddress = phone.getIpAddress();
        port = phone.getPort();
    }

    @Override
    public void makeCall() {

    }

    @Override
    public void answerCall() {

    }

    @Override
    public void clearConnection() {

    }

    @Override
    public void holdCall() {

    }

    @Override
    public void retrieveCall() {

    }
}

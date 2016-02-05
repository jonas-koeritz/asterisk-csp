package opencsp.asterisk;

import opencsp.Log;
import opencsp.csta.Provider;
import org.apache.commons.io.IOExceptionWithCause;
import org.asteriskjava.manager.*;
import org.asteriskjava.manager.action.ManagerAction;
import org.asteriskjava.manager.action.SipPeersAction;
import org.asteriskjava.manager.action.SipShowPeerAction;
import org.asteriskjava.manager.event.ManagerEvent;
import org.asteriskjava.manager.event.PeerEntryEvent;

public class Asterisk implements ManagerEventListener {
    private static final String TAG = "Asterisk";

    private String asteriskServer;
    private String amiUser;
    private String amiPassword;

    private Provider provider;

    private ManagerConnection managerConnection;

    public Asterisk(String asteriskServer, String amiUser, String amiPassword, Provider provider) throws AuthenticationFailedException, TimeoutException, java.io.IOException {
        this.provider = provider;
        this.asteriskServer = asteriskServer;
        this.amiUser = amiUser;
        this.amiPassword = amiPassword;

        managerConnection = (new ManagerConnectionFactory(asteriskServer, amiUser, amiPassword)).createManagerConnection();

        managerConnection.login();
        managerConnection.addEventListener(this);

        trySendAction(new SipPeersAction());
    }

    public void onManagerEvent(ManagerEvent event) {
        Log.d(TAG, "Event: " + event.toString());

        switch(event.getClass().getSimpleName()) {
            case "PeerEntryEvent":
                PeerEntryEvent p = (PeerEntryEvent)event;
                break;
            default:
                break;
        }
    }

    public void stop() {
        managerConnection.logoff();
    }


    private void trySendAction(ManagerAction action) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Sending Action: " + action.toString());
                    managerConnection.sendAction(action);

                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }).start();
    }
}

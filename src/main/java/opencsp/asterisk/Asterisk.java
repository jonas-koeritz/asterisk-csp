package opencsp.asterisk;

import opencsp.Log;
import opencsp.csta.Provider;
import opencsp.csta.types.Device;
import opencsp.csta.types.DeviceCategory;
import opencsp.csta.types.DeviceState;
import opencsp.devices.SIPPhone;
import org.apache.commons.io.IOExceptionWithCause;
import org.asteriskjava.manager.*;
import org.asteriskjava.manager.action.ManagerAction;
import org.asteriskjava.manager.action.SipPeersAction;
import org.asteriskjava.manager.action.SipShowPeerAction;
import org.asteriskjava.manager.event.ManagerEvent;
import org.asteriskjava.manager.event.PeerEntryEvent;
import org.asteriskjava.manager.event.PeerStatusEvent;

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
                PeerEntryEvent peerEntryEvent = (PeerEntryEvent)event;
                if(peerEntryEvent.getDynamic()) {
                    SIPPhone d = new SIPPhone(peerEntryEvent.getObjectName(), peerEntryEvent.getIpAddress(), peerEntryEvent.getIpPort());
                    if (peerEntryEvent.getStatus().contains("OK")) {
                        d.setState(DeviceState.Idle);
                    } else if (peerEntryEvent.getStatus().contains("UNKNOWN")) {
                        d.setState(DeviceState.Unknown);
                    } else if (peerEntryEvent.getStatus().contains("UNREACHABLE")) {
                        d.setState(DeviceState.Unavailable);
                    }
                    provider.addDevice(d);
                } else {
                    Device d = new Device(peerEntryEvent.getObjectName());
                    d.setCategory(DeviceCategory.NetworkInterface);
                    d.setState(DeviceState.Idle);
                    provider.addDevice(d);
                }
                break;
            case "PeerStatusEvent":
                PeerStatusEvent peerStatusEvent = (PeerStatusEvent)event;
                Device d = provider.findDeviceById(peerToDeviceId(peerStatusEvent.getPeer()));
                if(peerStatusEvent.getPeerStatus().equals("Registered")) {
                    if(d.getState().equals(DeviceState.Unavailable)) {
                        provider.backInService(d);
                    }
                    d.setState(DeviceState.Idle);
                } else if(peerStatusEvent.getPeerStatus().equals("Unregistered")) {
                    if(!d.getState().equals(DeviceState.Unavailable)) {
                        provider.outOfService(d);
                    }
                    d.setState(DeviceState.Unavailable);
                }
                break;
            default:
                break;
        }
    }

    private String peerToDeviceId(String peer) {
        return peer.split("/")[peer.split("/").length - 1];
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

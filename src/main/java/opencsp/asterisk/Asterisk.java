package opencsp.asterisk;

import opencsp.Log;
import opencsp.csta.Provider;
import opencsp.csta.types.Device;
import opencsp.csta.types.DeviceCategory;
import opencsp.csta.types.DeviceState;
import opencsp.devices.SIPPhone;
import org.apache.commons.io.IOExceptionWithCause;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.asteriskjava.manager.*;
import org.asteriskjava.manager.action.*;
import org.asteriskjava.manager.event.*;
import org.asteriskjava.manager.response.ManagerResponse;

import java.util.ArrayList;
import java.util.List;

public class Asterisk implements ManagerEventListener {
    private static final String TAG = "Asterisk";

    private String asteriskServer;
    private String amiUser;
    private String amiPassword;

    private Provider provider;

    private ManagerConnection managerConnection;
    private AsteriskServer asterisk;

    private List<PendingEventHandler> pendingEventHandlers;
    private int lastPendingActionId = 0;

    public Asterisk(String asteriskServer, String amiUser, String amiPassword, Provider provider) throws AuthenticationFailedException, TimeoutException, java.io.IOException {
        this.provider = provider;
        this.asteriskServer = asteriskServer;
        this.amiUser = amiUser;
        this.amiPassword = amiPassword;

        pendingEventHandlers = new ArrayList<PendingEventHandler>();

        asterisk = new DefaultAsteriskServer(asteriskServer, amiUser, amiPassword);
        managerConnection = asterisk.getManagerConnection();

        managerConnection.login();
        managerConnection.addEventListener(this);

        trySendAction(new SipPeersAction());
    }


    public void onManagerEvent(ManagerEvent event) {
        Log.d(TAG, "Event: " + event.toString());

        String eventClass = event.getClass().getSimpleName();

        //Run all pending event handlers
        if(event instanceof ResponseEvent) {
            ResponseEvent response = (ResponseEvent)event;
            pendingEventHandlers.stream().filter(h -> h.getActionId().equals(response.getActionId())).forEach(h -> h.onEvent(response));
            pendingEventHandlers.stream().filter(h -> h.getActionId().equals(response.getActionId())).forEach(h -> pendingEventHandlers.remove(h));
        }

        switch(eventClass) {
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

    public void putAsteriskDatabaseValue(String family, String key, String value) {
        DbPutAction action = new DbPutAction(family, key, value);
        trySendAction(action);
    }

    public void retrieveAsteriskDatabaseValue(String family, String key, OnAsteriskDatabaseValueRetrieved handler) {
        DbGetAction action = new DbGetAction(family, key);
        String actionId = "pending-" + lastPendingActionId++;
        action.setActionId(actionId);
        pendingEventHandlers.add(new PendingEventHandler(actionId) {
            @Override
            public void onEvent(ResponseEvent event) {
                DbGetResponseEvent r = (DbGetResponseEvent)event;
                handler.onValueRetrieved(r.getVal() != null ? r.getVal() : "");
            }
        });
        trySendAction(action);
    }

    public void trySendAction(ManagerAction action, SendActionCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Sending Action: " + action.toString());
                    if(callback != null) {
                        managerConnection.sendAction(action, callback);
                    } else {
                        managerConnection.sendAction(action);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }).start();
    }


    public void trySendAction(ManagerAction action) {
        trySendAction(action, null);
    }

    private static abstract class PendingEventHandler {
        private String actionId;

        public PendingEventHandler(String actionId) {
            this.actionId = actionId;
        }

        public String getActionId() {
            return actionId;
        }

        public abstract void onEvent(ResponseEvent event);
    }


    public static abstract class OnAsteriskDatabaseValueRetrieved {
        public abstract void onValueRetrieved(String value);
    }
}

package opencsp.asterisk;

import opencsp.Log;
import opencsp.csta.Provider;
import opencsp.csta.messages.EstablishedEvent;
import opencsp.csta.types.*;
import opencsp.devices.SIPPhone;
import org.apache.commons.io.IOExceptionWithCause;
import org.asteriskjava.live.*;
import org.asteriskjava.manager.*;
import org.asteriskjava.manager.action.*;
import org.asteriskjava.manager.event.*;
import org.asteriskjava.manager.response.ManagerResponse;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private boolean filterEvents(ManagerEvent e) {
        switch(e.getClass().getSimpleName()) {
            case "RtcpSentEvent":
            case "RtcpReceivedEvent":
            case "JabberEventEvent":
            case "NewExtenEvent":
            case "VarSetEvent":
                return false;
        }
        return true;
    }


    public void onManagerEvent(ManagerEvent event) {
        if(filterEvents(event)) {
            Log.d(TAG, "Event: " + event.toString());
        }

        String eventClass = event.getClass().getSimpleName();

        //Run all pending event handlers
        if(event instanceof ResponseEvent) {
            ResponseEvent response = (ResponseEvent)event;
            Iterator<PendingEventHandler> handlers = pendingEventHandlers.iterator();

            while(handlers.hasNext()) {
                PendingEventHandler h = handlers.next();
                if(h.getActionId().equals(response.getActionId())) {
                    h.onEvent(response);
                    handlers.remove();
                }
            }
        }

        switch(eventClass) {
            case "PeerEntryEvent":
                PeerEntryEvent peerEntryEvent = (PeerEntryEvent)event;
                if(peerEntryEvent.getDynamic()) {
                    SIPPhone d = new SIPPhone(peerEntryEvent.getObjectName(), peerEntryEvent.getIpAddress(), peerEntryEvent.getIpPort());
                    if (peerEntryEvent.getStatus() != null && peerEntryEvent.getStatus().contains("OK")) {
                        d.setState(DeviceState.Idle);
                    } else if (peerEntryEvent.getStatus().contains("UNKNOWN")) {
                        d.setState(DeviceState.Unknown);
                    } else if (peerEntryEvent.getStatus().contains("UNREACHABLE")) {
                        d.setState(DeviceState.Unavailable);
                    } else {
                        d.setState(DeviceState.Unknown);
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
                if(d != null) {
                    if (peerStatusEvent.getPeerStatus().equals("Registered")) {
                        if (d.getState().equals(DeviceState.Unavailable)) {
                            provider.backInService(d);
                        }
                        d.setState(DeviceState.Idle);
                    } else if (peerStatusEvent.getPeerStatus().equals("Unregistered")) {
                        if (!d.getState().equals(DeviceState.Unavailable)) {
                            provider.outOfService(d);
                        }
                        d.setState(DeviceState.Unavailable);
                    }
                }
                break;
            case "NewStateEvent":
                NewStateEvent newStateEvent = (NewStateEvent)event;
                Connection c = provider.getConnectionByUniqueId(newStateEvent.getUniqueId());
                Call call = provider.findCallForConnection(c);
                Device caller = null;

                if(c != null) {
                    switch(newStateEvent.getChannelStateDesc()) {
                        case "Ringing":
                            //Is there a connection already connected to this call?
                            if(call.getConnections().stream().filter(con -> con.getConnectionState().equals(ConnectionState.Connected)).count() > 0) {
                                Connection conA = call.getConnections().stream().filter(con -> con.getConnectionState().equals(ConnectionState.Connected)).findFirst().get();
                                Device callee = provider.findDeviceById(channelToDeviceId(newStateEvent.getChannel()));
                                caller = provider.findDeviceById(conA.getDeviceId());
                                provider.delivered(caller, provider.findDeviceById(callee.getDeviceId()), conA);
                            }
                            break;
                        case "Up":
                            if(call != null) {
                                //Is this an Accept scenario? One-Call should be in connected, one in any other state
                                if (call.getConnections().stream().filter(con -> !con.getConnectionState().equals(ConnectionState.Connected)).count() > 0 &&
                                    call.getConnections().stream().filter(con -> con.getConnectionState().equals(ConnectionState.Connected)).count() > 0) {
                                    Connection callee = call.getConnections().stream().filter(con -> !con.getConnectionState().equals(ConnectionState.Connected)).findFirst().get();
                                    Connection callingConnection = call.getConnections().stream().filter(con -> con.getConnectionState().equals(ConnectionState.Connected)).findFirst().get();
                                    Device callingDevice = provider.findDeviceById(callingConnection.getDeviceId());
                                    provider.established(callingDevice, provider.findDeviceById(callee.getDeviceId()), provider.findDeviceById(callee.getDeviceId()), callingConnection);
                                    callee.setConnectionState(ConnectionState.Connected);
                                }
                            }
                            break;
                    }
                }
                break;
            case "DialEvent":
                DialEvent dialEvent = (DialEvent)event;
                if(dialEvent.getSubEvent().equals(DialEvent.SUBEVENT_BEGIN)) {
                    provider.associateConnections(dialEvent.getUniqueId(), dialEvent.getDestUniqueId());

                    //Originating Device
                    Device dA = provider.findDeviceById(channelToDeviceId(dialEvent.getChannel()));
                    if(dA != null) {
                        provider.originated(dA, provider.getConnectionByUniqueId(dialEvent.getUniqueId()), channelToDeviceId(dialEvent.getDestination()));
                        provider.getConnectionByUniqueId(dialEvent.getUniqueId()).setConnectionState(ConnectionState.Connected);
                    }

                    //Destination Device
                    Device dB = provider.findDeviceById(channelToDeviceId(dialEvent.getDestination()));


                } else {
                    //Dialstatus is available (reason)
                }
                break;
            case "HangupRequestEvent":
                HangupRequestEvent hangupEvent = (HangupRequestEvent)event;
                Device clearingDevice = provider.findDeviceById(channelToDeviceId(hangupEvent.getChannel()));
                Connection clearedConnection = provider.getConnectionByUniqueId(hangupEvent.getUniqueId());
                if(clearedConnection != null) {
                    Call clearedCall = provider.getCallByCallId(clearedConnection.getCallId());
                    Iterator<Connection> clearedConnections = clearedCall.getConnections().iterator();
                    while (clearedConnections.hasNext()) {
                        Connection con = clearedConnections.next();
                        provider.connectionCleared(
                                provider.findDeviceById(con.getDeviceId()),
                                clearingDevice,
                                con
                        );
                        clearedConnections.remove();
                        provider.removeConnection(con);
                    }
                    provider.removeCall(clearedCall);
                }
                break;
            case "HoldEvent":
                HoldEvent holdEvent = (HoldEvent)event;
                Device holdDevice = provider.findDeviceById(channelToDeviceId(holdEvent.getChannel()));
                Connection holdConnection = provider.getConnectionByUniqueId(holdEvent.getUniqueId());
                if(holdEvent.getStatus()) {
                    provider.held(holdDevice, holdConnection);
                    holdConnection.setConnectionState(ConnectionState.Hold);
                } else {
                    provider.retrieved(holdDevice, holdConnection);
                    holdConnection.setConnectionState(ConnectionState.Connected);
                }

                break;
            case "NewChannelEvent":
                NewChannelEvent newChannelEvent = (NewChannelEvent)event;
                provider.newConnection(channelToDeviceId(newChannelEvent.getChannel()), newChannelEvent.getUniqueId());
                break;
            case "MasqueradeEvent":
                MasqueradeEvent masqueradeEvent = (MasqueradeEvent)event;
                //This is a direct Pick-Up
                if(masqueradeEvent.getOriginalStateDesc().equals("Ringing") &&
                                masqueradeEvent.getCloneStateDesc().equals("Up")
                        ) {
                    Device original = provider.findDeviceById(channelToDeviceId(masqueradeEvent.getOriginal()));
                    Device clone = provider.findDeviceById(channelToDeviceId(masqueradeEvent.getClone()));
                    List<Connection> originalConnections = provider.findConnectionsForDevice(original);
                    List<Connection> offeredConnections = originalConnections.stream().filter(origCon -> origCon.getConnectionState() != ConnectionState.Connected).collect(Collectors.toList());

                    List<Connection> cloneConnections = provider.findConnectionsForDevice(clone);

                    if(offeredConnections.size() > 0 && cloneConnections.size() > 0) {
                        Connection originalConnection = offeredConnections.get(0);

                        Call incomingCall = provider.getCallByCallId(originalConnection.getCallId());

                        Log.d(TAG, "Pick-Up: " + clone + " is picking up " + originalConnection);
                        provider.connectionCleared(original, original, originalConnection);
                        incomingCall.getConnections().remove(originalConnection);
                        provider.removeConnection(originalConnection);

                        Connection cloneConnection = cloneConnections.get(0);
                        List<Connection> incomingConnections = incomingCall.getConnections();

                        if(incomingConnections.size() > 0) {
                            //There should only be one connection left in this call
                            Connection incomingConnection = incomingConnections.get(0);
                            //Add the picking-up connection to this call
                            cloneConnection.setCallId(originalConnection.getCallId());
                            incomingCall.addConnection(cloneConnection);

                            provider.established(
                                    provider.findDeviceById(incomingConnection.getDeviceId()),
                                    original,
                                    clone,
                                    cloneConnection
                            );
                        } else {
                            Log.e(TAG, "There is no connection left in the call.");
                        }

                    } else {
                        Log.w(TAG, "There are no half-open connections to Pick-Up. Or the picking-up device has no connection.");
                    }
                }

                break;
            default:
                break;
        }
    }

    private DeviceId channelToDeviceId(String channel) {
        String withoutTechnology = (channel.split("/")[channel.split("/").length - 1]);
        return new DeviceId(withoutTechnology.split("-")[0]);
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

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
        managerConnection.registerUserEventClass(EndpointListCompleteEvent.class);
        managerConnection.registerUserEventClass(EndpointListEvent.class);
        managerConnection.registerUserEventClass(DialBeginEvent.class);


        managerConnection.login();
        managerConnection.addEventListener(this);

        trySendAction(new SipPeersAction());
        trySendAction(new PJSIPShowEndpointsAction());
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
            case "EndpointListEvent":
                EndpointListEvent endpointListEvent = (EndpointListEvent)event;
                if(endpointListEvent.getAuths() != null) {
                    SIPPhone d = new SIPPhone(endpointListEvent.getObjectName(), endpointListEvent.getIpAddress(), endpointListEvent.getIpPort());
                    if(endpointListEvent.getDeviceState() != null && endpointListEvent.getDeviceState().contains("Not in use")) {
                        d.setState(DeviceState.Idle);
                    } else if(endpointListEvent.getDeviceState().contains("In use")) {
                        d.setState(DeviceState.InUse);
                    } else if(endpointListEvent.getDeviceState().contains("Unavailable")) {
                        d.setState(DeviceState.Unavailable);
                    } else {
                        d.setState(DeviceState.Unknown);
                    }
                    provider.addDevice(d);
                } else {
                    Device d = new Device(endpointListEvent.getObjectName());
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
                Call call = provider.getCallByCallId(c.getCallId());


                Device caller = null;

                if(c != null) {
                    if(provider.findDeviceById(c.getDeviceId()).getCategory() == DeviceCategory.Station) {
                        provider.findDeviceById(c.getDeviceId()).setState(DeviceState.fromString(newStateEvent.getChannelStateDesc()));
                    }

                    switch(newStateEvent.getChannelStateDesc()) {
                        case "Ringing":
                            if(call == null) {
                                //There has to be a call this device is participating in
                                Iterator<Call> allCalls = provider.getCalls().iterator();
                                while(allCalls.hasNext()) {
                                    Call callToCheck = allCalls.next();
                                    if(callToCheck.getConnections().stream().filter(checkedCon -> checkedCon.getDeviceId().toString().equals(channelToDeviceId(newStateEvent.getChannel()).toString())).count() > 0) {
                                        call = callToCheck;
                                        break;
                                    }
                                }
                            }

                            if(call != null) {
                                //Is there a connection already connected to this call?
                                if (call.getConnections().stream().filter(con -> con.getConnectionState().equals(ConnectionState.Connected)).count() > 0) {
                                    Connection conA = call.getConnections().stream().filter(con -> con.getConnectionState().equals(ConnectionState.Connected)).findFirst().get();
                                    Connection conB = call.getConnections().stream().filter(con -> !con.getConnectionState().equals(ConnectionState.Connected)).findFirst().get();
                                    Device callee = provider.findDeviceById(channelToDeviceId(newStateEvent.getChannel()));
                                    caller = provider.findDeviceById(conA.getDeviceId());
                                    String conAPresentation = conA.getPresentation().length() > 0 ? conA.getPresentation() : null;
                                    String conBPresentation = conB.getPresentation().length() > 0 ? conB.getPresentation() : null;

                                    provider.delivered(caller, provider.findDeviceById(callee.getDeviceId()), conA, conAPresentation, conBPresentation);
                                    provider.offered(caller, provider.findDeviceById(callee.getDeviceId()), conB, conAPresentation, conBPresentation);
                                    provider.delivered(caller, provider.findDeviceById(callee.getDeviceId()), conB, conAPresentation, conBPresentation);
                                }


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
                                    String callingConnectionPresentation = callingConnection.getPresentation().length() > 0 ? callingConnection.getPresentation() : null;
                                    String calleePresentation = callee.getPresentation().length() > 0 ? callee.getPresentation() : null;
                                    provider.established(callingDevice, provider.findDeviceById(callee.getDeviceId()), provider.findDeviceById(callee.getDeviceId()), callingConnection, callingConnectionPresentation, calleePresentation, calleePresentation);
                                    callee.setConnectionState(ConnectionState.Connected);
                                }
                            }
                            break;
                    }
                }
                break;
            case "DialEvent":
                DialEvent dialEvent = (DialEvent)event;
                //Ignore internal dialing
                if(getChannelType(dialEvent.getChannel()).equals("Local"))
                    break;

                if(dialEvent.getSubEvent().equals(DialEvent.SUBEVENT_BEGIN)) {
                    provider.associateConnections(dialEvent.getUniqueId(), dialEvent.getDestUniqueId());

                    //Originating Device
                    Device dA = provider.findDeviceById(channelToDeviceId(dialEvent.getChannel()));
                    Device dB = provider.findDeviceById(channelToDeviceId(dialEvent.getDestination()));
                    Connection outgoingConnection = provider.getConnectionByUniqueId(dialEvent.getDestUniqueId());

                    if(dB.getCategory().equals(DeviceCategory.NetworkInterface)) {
                        outgoingConnection.setPresentation(dialEvent.getConnectedlinenum());
                    }

                    if(dA != null) {
                        provider.originated(
                                dA,
                                provider.getConnectionByUniqueId(dialEvent.getUniqueId()),
                                channelToDeviceId(dialEvent.getDestination()),
                                outgoingConnection.getPresentation().length() > 0 ? outgoingConnection.getPresentation() : null
                        );
                        provider.getConnectionByUniqueId(dialEvent.getUniqueId()).setConnectionState(ConnectionState.Connected);
                    }

                    //Destination Device


                } else {
                    //Dialstatus is available (reason)
                }
                break;
            case "NewCallerIdEvent":
                NewCallerIdEvent newCallerIdEvent = (NewCallerIdEvent)event;
                Connection newCallerIdConnection = provider.getConnectionByUniqueId(newCallerIdEvent.getUniqueId());
                if(newCallerIdConnection != null) {
                    Device newCallerIdDevice = provider.findDeviceById(newCallerIdConnection.getDeviceId());
                    if(newCallerIdDevice != null && newCallerIdDevice.getCategory().equals(DeviceCategory.NetworkInterface)) {
                        newCallerIdConnection.setPresentation(newCallerIdEvent.getCallerIdNum());
                        Log.d(TAG, "Set new presentation for Connection " + newCallerIdConnection.toString() + ": " + newCallerIdEvent.getCallerIdNum());
                    }
                }
                break;
            case "DialBeginEvent":
                DialBeginEvent dialBeginEvent = (DialBeginEvent)event;
                if(getChannelType(dialBeginEvent.getChannel()).equals("Local"))
                    break;

                provider.associateConnections(dialBeginEvent.getUniqueId(), dialBeginEvent.getDestUniqueId());

                //Originating Device
                Device dA = provider.findDeviceById(channelToDeviceId(dialBeginEvent.getChannel()));
                Device dB = provider.findDeviceById(channelToDeviceId(dialBeginEvent.getDestChannel()));
                Connection outgoingConnection = provider.getConnectionByUniqueId(dialBeginEvent.getDestUniqueId());

                if(dB.getCategory().equals(DeviceCategory.NetworkInterface)) {
                    outgoingConnection.setPresentation(dialBeginEvent.getConnectedLineNum());
                }

                if(dA != null) {
                    provider.originated(
                            dA,
                            provider.getConnectionByUniqueId(dialBeginEvent.getUniqueId()),
                            channelToDeviceId(dialBeginEvent.getDestChannel()),
                            outgoingConnection.getPresentation().length() > 0 ? outgoingConnection.getPresentation() : null
                    );
                    provider.getConnectionByUniqueId(dialBeginEvent.getUniqueId()).setConnectionState(ConnectionState.Connected);
                }
                break;
            case "HangupRequestEvent":
                HangupRequestEvent hangupRequestEvent = (HangupRequestEvent)event;
                Device clearingDevice = provider.findDeviceById(channelToDeviceId(hangupRequestEvent.getChannel()));
                Connection clearedConnection = provider.getConnectionByUniqueId(hangupRequestEvent.getUniqueId());

                if(clearedConnection != null) {
                    Call clearedCall = provider.getCallByCallId(clearedConnection.getCallId());
                    if(clearedCall != null) {
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
                }
                break;
            case "HangupEvent":
                HangupEvent hangupEvent = (HangupEvent)event;
                Device hangupDevice = provider.findDeviceById(channelToDeviceId(hangupEvent.getChannel()));

                if(provider.findDeviceById(hangupDevice.getDeviceId()).getCategory() == DeviceCategory.Station) {
                    provider.findDeviceById(hangupDevice.getDeviceId()).setState(DeviceState.Idle);
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
                Device newChannelDevice = provider.findDeviceById(channelToDeviceId(newChannelEvent.getChannel()));

                if(provider.findDeviceById(newChannelDevice.getDeviceId()).getCategory() == DeviceCategory.Station) {
                    provider.findDeviceById(newChannelDevice.getDeviceId()).setState(DeviceState.fromString(newChannelEvent.getChannelStateDesc()));
                }

                Connection newChannelConnection = provider.newConnection(channelToDeviceId(newChannelEvent.getChannel()), newChannelEvent.getUniqueId());
                if(newChannelDevice.getCategory().equals(DeviceCategory.NetworkInterface)) {
                    newChannelConnection.setPresentation(newChannelEvent.getCallerIdNum());
                }

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
                                    cloneConnection,
                                    incomingConnection.getPresentation().length() > 0 ? incomingConnection.getPresentation() : null,
                                    cloneConnection.getPresentation().length() > 0 ? cloneConnection.getPresentation() : null,
                                    cloneConnection.getPresentation().length() > 0 ? cloneConnection.getPresentation() : null
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

    private String getChannelType(String channel) {
        return channel.split("/")[0];
    }

    private DeviceId channelToDeviceId(String channel) {
        String withoutTechnology = (channel.split("/")[channel.split("/").length - 1]);
        String withoutContext = withoutTechnology.split("@")[0];
        return new DeviceId(withoutContext.split("-")[0]);
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

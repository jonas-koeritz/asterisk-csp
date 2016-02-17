package opencsp.csta.types;

import io.netty.channel.Channel;
import opencsp.Log;
import opencsp.csta.CSTASessionManager;
import opencsp.csta.messages.MonitorStart;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class CSTASession {
    private static final String TAG = "CSTASession";

    private static final int TIMEOUT_FACTOR = 2;

    private int sessionId;
    private ProtocolVersion protocolVersion;
    private int sessionDuration;
    private Channel clientChannel;


    private List<MonitorPoint> monitorPoints;

    ScheduledExecutorService timeoutExecutor;
    private Timer timeoutWarningTimer;
    private Timer timeoutTimer;

    public OnSessionTimeoutHandler timeoutHandler;

    private int lastCrossReferenceId = 0;

    public CSTASession(int sessionId, ProtocolVersion protocolVersion, int sessionDuration, Channel sourceChannel) {
        this.sessionId = sessionId;
        this.protocolVersion = protocolVersion;
        this.sessionDuration = sessionDuration;
        this.clientChannel = sourceChannel;

        this.timeoutExecutor = Executors.newScheduledThreadPool(2);
        this.monitorPoints = new ArrayList<MonitorPoint>();
        scheduleTimeout();
    }

    public MonitorPoint createMonitorPoint(Device device) {
        MonitorPoint m = new MonitorPoint(new CrossReferenceId(lastCrossReferenceId++), device);
        monitorPoints.add(m);
        Log.d(TAG, "Created new MonitorPoint in session " + sessionId + ": " + m.toString());
        return m;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setOnSessionTimeoutHandler(OnSessionTimeoutHandler handler) {
        this.timeoutHandler = handler;
    }

    private void scheduleTimeout() {
        timeoutTimer = new Timer();
        CSTASession session = this;
        timeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "Closing CSTA connection, client (" + clientChannel.remoteAddress().toString() + ") failed to refresh the session.");
                clientChannel.close();
                timeoutHandler.onSessionTimeout(session);
            }
        }, sessionDuration * 1000 * TIMEOUT_FACTOR);

        timeoutWarningTimer = new Timer();
        timeoutWarningTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "CSTA Client (" + (clientChannel != null ? clientChannel.remoteAddress().toString() : "") + ") failed to refresh session.");
            }
        }, sessionDuration * 1000 + 10000);
    }

    public void resetSessionTimeout(int sessionDuration) {
        this.sessionDuration = sessionDuration;
        timeoutTimer.cancel();
        timeoutWarningTimer.cancel();
        scheduleTimeout();
    }

    public void cancelSessionTimeouts() {
        timeoutTimer.cancel();
        timeoutWarningTimer.cancel();
    }

    public MonitorPoint getMonitorPointForDevice(DeviceId deviceId) {
        return getMonitorPointForDevice(deviceId.toString());
    }

    public MonitorPoint findMonitorPointByCrossReferenceId(CrossReferenceId id) {
        if(monitorPoints.stream().filter(m -> m.getCrossReferenceId().equals(id)).count() > 0) {
            return monitorPoints.stream().filter(m -> m.getCrossReferenceId().equals(id)).findFirst().get();
        } else {
            return null;
        }
    }

    public void removeMonitorPoint(MonitorPoint m) {
        monitorPoints.remove(m);
        Log.d(TAG, "Removed Monitor Point in Session " + sessionId + ": " + m.toString());
    }

    public MonitorPoint getMonitorPointForDevice(String deviceId) {
        if(monitorPoints.stream().filter(m -> m.getMonitoredDevice().getDeviceId().toString().equals(deviceId)).count() > 0) {
            return monitorPoints.stream().filter(m -> m.getMonitoredDevice().getDeviceId().toString().equals(deviceId)).findFirst().get();
        } else {
            return null;
        }
    }

    public Channel getClientChannel() {
        return clientChannel;
    }

    public interface OnSessionTimeoutHandler {
        public void onSessionTimeout(CSTASession session);
    }
}

package opencsp.csta;

import opencsp.Log;
import opencsp.csta.messages.ResetApplicationSessionTimerPosResponse;
import opencsp.csta.messages.StopApplicationSessionPosResponse;
import opencsp.csta.types.CSTASession;
import opencsp.csta.xml.CSTAXmlSerializable;

import java.util.ArrayList;
import java.util.List;

public class CSTASessionManager implements CSTASession.OnSessionTimeoutHandler {
    private static final String TAG = "CSTASessionManager";

    private List<CSTASession> sessions;

    public CSTASessionManager() {
        sessions = new ArrayList<>();
    }

    public void newSession(CSTASession session) {
        session.setOnSessionTimeoutHandler(this);
        sessions.add(session);
        Log.i(TAG, "Managing new CSTASession with sessionId " + session.getSessionId());
    }

    public void onSessionTimeout(CSTASession session) {
        Log.w(TAG, "Session timeout and grace period elapsed. Removing session with sessionId " + session.getSessionId());
        sessions.remove(session);
    }

    public CSTAXmlSerializable resetSessionTimers(int sessionId, int sessionTimeout) {
        Log.d(TAG, "Reset session timeout for sessionId " + sessionId + ", next timeout in " + sessionTimeout + " seconds");
        sessions.stream().filter(s -> s.getSessionId() == sessionId).forEach(s -> s.resetSessionTimeout(sessionTimeout));
        return new ResetApplicationSessionTimerPosResponse(sessionTimeout);
    }

    public CSTAXmlSerializable removeSession(CSTASession session) {
        Log.i(TAG, "Removing session with sessionId " + session.getSessionId());
        sessions.remove(session);
        session.cancelSessionTimeouts();
        return new StopApplicationSessionPosResponse();
    }

    public CSTASession getSessionById(int sessionId) {
        if(sessions.stream().filter(s -> s.getSessionId() == sessionId).count() > 0) {
            return sessions.stream().filter(s -> s.getSessionId() == sessionId).findFirst().get();
        } else {
            return null;
        }
    }

    public List<CSTASession> getSessions() {
        return sessions;
    }
}

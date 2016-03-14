package opencsp.asterisk;

import org.asteriskjava.manager.action.AbstractManagerAction;
import org.asteriskjava.manager.action.EventGeneratingAction;
import org.asteriskjava.manager.event.ResponseEvent;

public class PJSIPShowEndpointsAction extends AbstractManagerAction implements EventGeneratingAction {
    private String actionId;

    public String getAction() {
        return "PJSIPShowEndpoints";
    }

    public String getActionId() {
        return this.actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public Class<? extends ResponseEvent> getActionCompleteEventClass() {
        return EndpointListCompleteEvent.class;
    }
}

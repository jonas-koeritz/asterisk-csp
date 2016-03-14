package opencsp.asterisk;

import org.asteriskjava.manager.event.ResponseEvent;

public class EndpointListCompleteEvent extends ResponseEvent {
    public EndpointListCompleteEvent(Object source) {
        super(source);
    }


    private String eventList;
    private String listItems;

    public String getEventList() {
        return eventList;
    }

    public void setEventList(String eventList) {
        this.eventList = eventList;
    }

    public String getListItems() {
        return listItems;
    }

    public void setListItems(String listItems) {
        this.listItems = listItems;
    }
}

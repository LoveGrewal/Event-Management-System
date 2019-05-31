package concordia.dems.business.impl;

import concordia.dems.business.IEventManagerBusiness;
import concordia.dems.database.IEventManagerDatabase;
import concordia.dems.helpers.Helper;
import concordia.dems.model.Event;
import concordia.dems.model.enumeration.EventBatch;
import concordia.dems.model.enumeration.EventType;

import java.util.List;

public class EventManagerBusinessTorontoImpl implements IEventManagerBusiness {

    private Event event;
    private final int EVENT_ID_INDEX = 0;
    private final int EVENT_TYPE_INDEX = 1;
    private final int EVENT_CAPACITY_INDEX = 2;
    private final int EVENT_TIME_SLOT_INDEX = 3;

    private static IEventManagerDatabase iEventManagerDatabase;

    public EventManagerBusinessTorontoImpl() {
    }

    /*
       Manager Related Functions
    */

    @Override
    public Boolean addEvent(String addEventInfo) {
        // Format:(EVENTID FORMAT)[city,timslot,eventdate] : TORE100519,Enumeration Type,bookingcapacity
        // Invoke Database Method of Add Event
        return null;
    }

    @Override
    public Boolean removeEvent(String removeEventInfo) {
        // Format:(EVENTID FORMAT)[city,timslot,eventdate] : TORE100519,Enumeration Type
        String[] eventInformation = "TORE100519,Conference".split(",");
        EventType eventType = Helper.getEventTypeEnumObject(eventInformation[EVENT_TYPE_INDEX]);
        event = new Event(eventInformation[EVENT_ID_INDEX], eventType);
        // Invoke Database Method of Remove Event
        return null;
    }

    @Override
    public List<Event> listEventAvailability(String eventType) {
        EventType typeOfEvent = Helper.getEventTypeEnumObject(eventType);
        //Invoke Database Method of Listing Event Availability
        return null;
    }

    /*
        Customer Related Functions
     */

    @Override
    public Boolean bookEvent(String eventBookingInfo) {
        return null;
    }

    @Override
    public List<Event> getBookingSchedule(String customerID) {
        return null;
    }

    @Override
    public Boolean cancelEvent(String cancelEventInfo) {
        return null;
    }
}

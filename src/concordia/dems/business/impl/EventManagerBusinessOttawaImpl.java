package concordia.dems.business.impl;

import concordia.dems.business.IEventManagerBusiness;
import concordia.dems.database.IEventManagerDatabase;
import concordia.dems.helpers.Helper;
import concordia.dems.model.Event;
import concordia.dems.model.enumeration.EventBatch;
import concordia.dems.model.enumeration.EventType;

import java.util.List;

public class EventManagerBusinessOttawaImpl implements IEventManagerBusiness {

    private static IEventManagerDatabase iEventManagerDatabase;

    private Event event;
    private final int EVENT_ID_INDEX = 0;
    private final int EVENT_TYPE_INDEX = 1;
    private final int EVENT_CAPACITY_INDEX = 2;
    private final int EVENT_TIME_SLOT_INDEX = 3;

    /*
       Manager Related Functions
    */

    @Override
    public Boolean addEvent(String addEventInfo) {
        // dummy:city,timslot,eventdate : TORE100519,Enumeration Type,bookingcapacity
        String[] eventInformation = "OTWE100519,Conference,20".split(",");
        // At index 3 of eventId, the letter represents event batch
        EventBatch eventBatch = Helper.getEventBatchEnumObject(eventInformation[EVENT_ID_INDEX].charAt(EVENT_TIME_SLOT_INDEX));
        EventType eventType = Helper.getEventTypeEnumObject(eventInformation[EVENT_TYPE_INDEX]);
        event = new Event(eventInformation[EVENT_ID_INDEX], eventType, eventBatch, Integer.parseInt(eventInformation[EVENT_CAPACITY_INDEX]));
        return null;
    }

    @Override
    public Boolean removeEvent(String removeEventInfo) {
        return null;
    }

    @Override
    public List<Event> listEventAvailability(String eventType) {
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

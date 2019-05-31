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

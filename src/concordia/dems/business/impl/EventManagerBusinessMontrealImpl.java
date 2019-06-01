package concordia.dems.business.impl;

import concordia.dems.business.IEventManagerBusiness;
import concordia.dems.database.IEventManagerDatabase;
import concordia.dems.database.impl.EventManagerDatabaseMontrealImpl;
import concordia.dems.helpers.Helper;
import concordia.dems.model.Event;
import concordia.dems.model.enumeration.EventType;

import java.util.Collections;
import java.util.List;

public class EventManagerBusinessMontrealImpl implements IEventManagerBusiness {

    private static IEventManagerDatabase iEventManagerDatabase;

    public EventManagerBusinessMontrealImpl() {
        iEventManagerDatabase = new EventManagerDatabaseMontrealImpl();
    }
    /*
       Manager Related Functions
    */

    @Override
    public Boolean addEvent(String addEventInfo) {
        String[] unWrappingRequest = addEventInfo.split(",");
        //0 = EventID , 1 = Event Type , 2 = Event Batch , 3 = Booking Capacity
        Event event = new Event(unWrappingRequest[0],
                Helper.getEventTypeEnumObject(unWrappingRequest[1]),
                Helper.getEventBatchEnumObject(unWrappingRequest[2]),
                Integer.parseInt(unWrappingRequest[3]));
        return iEventManagerDatabase.addEvent(event);
    }

    @Override
    public Boolean removeEvent(String removeEventInfo) {
        String[] unWrappingRequest = removeEventInfo.split(",");
        Event e = new Event(unWrappingRequest[0], Helper.getEventTypeEnumObject(unWrappingRequest[1]));
        return iEventManagerDatabase.removeEvent(e);
    }

    @Override
    public List<Event> listEventAvailability(String eventType) {
        EventType eventTypeObj = Helper.getEventTypeEnumObject(eventType);
        return iEventManagerDatabase.listEventAvailability(eventTypeObj);
    }

    /*
       Customer Related Functions
    */

    @Override
    public Boolean bookEvent(String eventBookingInfo) {
        String[] unWrappingRequest = eventBookingInfo.split(",");
        String customerID = unWrappingRequest[0];
        String eventID = unWrappingRequest[1];
        EventType eventType = Helper.getEventTypeEnumObject(unWrappingRequest[2]);
        return iEventManagerDatabase.bookEvent(customerID, eventID, eventType);
    }

    @Override
    public List<Event> getBookingSchedule(String customerID) {
        System.err.println("Booking Schedule Info : " + customerID);
        return null;
    }

    @Override
    public Boolean cancelEvent(String cancelEventInfo) {
        return null;
    }
}

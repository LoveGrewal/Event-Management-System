package concordia.dems.database;

import concordia.dems.model.Event;

import java.util.List;

/**
 * @author Loveshant
 * @version 1.0.0
 */
public interface IEventManagerDatabase {
    Boolean addEvent (String eventID, String eventType, String bookingCapacity);
    Boolean removeEvent (String eventID, String eventType);
    List<Event> listEventAvailability (String eventType);
    Boolean bookEvent (String customerID,String eventID,String eventType);
    List<Event> getBookingSchedule (String customerID);
    Boolean cancelEvent (String customerID,String eventID);
}

package concordia.dems.database;

import concordia.dems.model.Event;
import concordia.dems.model.enumeration.EventType;

import java.util.List;

/**
 * @author Loveshant
 * @version 1.0.0
 */
public interface IEventManagerDatabase {
    Boolean addEvent (Event event);
    Boolean removeEvent (Event event);
    List<Event> listEventAvailability (EventType eventType);
    Boolean bookEvent (String customerID,String eventID,EventType eventType);
    List<Event> getBookingSchedule (String customerID);
    Boolean cancelEvent (String customerID,String eventID);
}

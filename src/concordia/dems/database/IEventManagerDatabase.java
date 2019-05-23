package concordia.dems.database;

public interface IEventManagerDatabase {
    String addEvent (String eventID, String eventType, String bookingCapacity);
    String removeEvent (String eventID, String eventType);
    String listEventAvailability (String eventType);
    String bookEvent (String customerID,String eventID,String eventType);
    String getBookingSchedule (String customerID);
    String cancelEvent (String customerID,String eventID);
}

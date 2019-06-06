package concordia.dems.database.impl;

import concordia.dems.database.IEventManagerDatabase;
import concordia.dems.helpers.Constants;
import concordia.dems.helpers.Logger;
import concordia.dems.model.Event;
import concordia.dems.model.enumeration.EventBatch;
import concordia.dems.model.enumeration.EventType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventManagerDatabaseOttawaImpl implements IEventManagerDatabase {

    private static Map<EventType, Map<String, Event>> eventData = new ConcurrentHashMap<>();

    static {
        Map<String, Event> temp = new ConcurrentHashMap<>();
        temp.put("OTWA120619", new Event("OTWA120619", EventType.SEMINAR, EventBatch.AFTERNOON, 35));
        temp.put("OTWM130619", new Event("OTWM130619", EventType.SEMINAR, EventBatch.MORNING, 35));
        eventData.put(EventType.CONFERENCE, new ConcurrentHashMap<>());
        eventData.put(EventType.SEMINAR, temp);
        eventData.put(EventType.TRADESHOW, new ConcurrentHashMap<>());
    }

    /**
     * @param event object details
     * @return Boolean True if new event added false otherwise(but booking capacity is updated)
     */
    @Override
    public Boolean addEvent(Event event) {
        if (eventData.get(event.getEventType()).containsKey(event.getEventId())) {
            eventData.get(event.getEventType()).get(event.getEventId()).setBookingCapacity(event.getBookingCapacity());
            Logger.writeLogToFile("server", "ottawaServer", "addEvent", "updated", Constants.TIME_STAMP);
            return Boolean.FALSE;
        }
        eventData.get(event.getEventType()).put(event.getEventId(), event);
        Logger.writeLogToFile("server", "ottawaServer", "addEvent", "added", Constants.TIME_STAMP);
        return Boolean.TRUE;
    }

    /**
     * @param event object details
     * @return Boolean true if removed and false otherwise
     */
    @Override
    public Boolean removeEvent(Event event) {
        if (eventData.get(event.getEventType()).containsKey(event.getEventId())) {
            eventData.get(event.getEventType()).remove(event.getEventId());
            Logger.writeLogToFile("server", "ottawaServer", "removeEvent", "removed : " + event.getEventId(), Constants.TIME_STAMP);
            return Boolean.TRUE;
        }
        Logger.writeLogToFile("server", "ottawaServer", "removeEvent", "event id not found : " + event.getEventId(), Constants.TIME_STAMP);
        return Boolean.FALSE;
    }

    /**
     * @param eventType object details
     * @return list of events
     */
    @Override
    public List<Event> listEventAvailability(EventType eventType) {
        List<Event> eventList = new ArrayList<>();
        Iterator iterator = eventData.get(eventType).entrySet().iterator();
        Event e;
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            e = (Event) pair.getValue();
            eventList.add(e);
        }
        Logger.writeLogToFile("server", "ottawaServer", "listEventAvailability", "fetch and sent", Constants.TIME_STAMP);
        return eventList;
    }

    /**
     * @param customerID customer id
     * @param eventID    event id
     * @param eventType  event type
     * @return will deduct remaining capacity and add customerID to event customer list
     */
    @Override
    public Boolean bookEvent(String customerID, String eventID, EventType eventType) {
        if (eventData.get(eventType).containsKey(eventID)) {
            if (eventData.get(eventType).get(eventID).getRemainingCapacity() > 0) {
                eventData.get(eventType).get(eventID).addCustomer(customerID);
                eventData.get(eventType).get(eventID).setRemainingCapacity(eventData.get(eventType).get(eventID).getBookingCapacity() - 1);
                Logger.writeLogToFile("server", "ottawaServer", "bookEvent", "event booked for customer " + customerID, Constants.TIME_STAMP);
                return Boolean.TRUE;
            } else {
                Logger.writeLogToFile("server", "ottawaServer", "bookEvent", "Capacity is full", Constants.TIME_STAMP);
                return false;
            }
        }
        Logger.writeLogToFile("server", "ottawaServer", "bookEvent", "no such event found for event id " + eventID, Constants.TIME_STAMP);
        return Boolean.FALSE;
    }

    /**
     * @param customerID customer id
     * @return all events related to customerID
     */
    @Override
    public List<Event> getBookingSchedule(String customerID) {
        List<Event> eventList = new ArrayList<>();
        Iterator it;
        Event e;
        // iterate over enums using for loop
        for (EventType eventType : EventType.values()) {
            it = eventData.get(eventType).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                e = (Event) pair.getValue();
                if (e.getCustomers().contains(customerID))
                    eventList.add(e);
            }
        }
        Logger.writeLogToFile("server", "ottawaServer", "getBookingSchedule", "booking schedule sent for customer " + customerID, Constants.TIME_STAMP);
        return eventList;
    }

    /**
     * @param customerID customer id
     * @param eventID    event id
     * @return true when event exist with customer and false if there is no event
     */
    @Override
    public Boolean cancelEvent(String customerID, String eventID) {
        boolean found = false;
        Event eventToCancel = null;
        Event e;
        Iterator it;
        for (EventType eventType : EventType.values()) {
            it = eventData.get(eventType).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                e = (Event) pair.getValue();
                if (e.findIfCustomerPresent(customerID)) {
                    eventToCancel = e;
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }
        if (eventToCancel != null) {
            eventToCancel.setRemainingCapacity(eventToCancel.getRemainingCapacity() + 1);
            eventToCancel.removeCustomer(customerID);
            Logger.writeLogToFile("server", "ottawaServer", "cancelEvent", "event cancel for " + customerID + " in " + eventID, Constants.TIME_STAMP);
            return Boolean.TRUE;
        }
        Logger.writeLogToFile("server", "ottawaServer", "cancelEvent", "event cancel rejected for " + customerID + " in " + eventID + " event not found", Constants.TIME_STAMP);
        return Boolean.FALSE;
    }

    /**
     * @param eventID event id
     * @return remaining capacity of event if exist and -1 if event is not present
     */
    @Override
    public int getRemainingCapacityOfEvent(String eventID) {
        Iterator it;
        Event e;
        for (EventType eventType : EventType.values()) {
            if (eventData.get(eventType).containsKey(eventID)) {
                it = eventData.get(eventType).entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    e = (Event) pair.getValue();
                    if (e.getEventId().equals(eventID)) {
                        return e.getRemainingCapacity();
                    }
                }
            }
        }
        return -1;
    }


}

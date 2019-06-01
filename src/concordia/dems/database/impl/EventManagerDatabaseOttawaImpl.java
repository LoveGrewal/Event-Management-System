package concordia.dems.database.impl;

import concordia.dems.database.IEventManagerDatabase;
import concordia.dems.model.Event;
import concordia.dems.model.enumeration.EventType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventManagerDatabaseOttawaImpl implements IEventManagerDatabase {

    private static Map<EventType, Map<String, Event>> eventData = new ConcurrentHashMap<>();

    static {
        eventData.put(EventType.CONFERENCE, new ConcurrentHashMap<>());
        eventData.put(EventType.SEMINAR, new ConcurrentHashMap<>());
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
            return Boolean.FALSE;
        }
        eventData.get(event.getEventType()).put(event.getEventId(), event);
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
            return Boolean.TRUE;
        }
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
            eventData.get(eventType).get(eventID).addCustomer(customerID);
            eventData.get(eventType).get(eventID).setRemainingCapacity(eventData.get(eventType).get(eventID).getBookingCapacity() - 1);
            return Boolean.TRUE;
        }
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
                eventList.add(e);
            }
        }
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
            return Boolean.TRUE;
        }
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

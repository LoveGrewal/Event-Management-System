package concordia.dems.model;

import concordia.dems.model.enumeration.EventBatch;
import concordia.dems.model.enumeration.EventType;

import java.util.List;

/**
 * This Event POJO class contains event information
 *
 * @author Mayank Jariwala
 * @version 1.0.0
 */
public class Event {

    private String eventId;
    private EventType eventType;
    private int bookingCapacity, remainingCapacity;
    private List<String> customers;
    private EventBatch eventBatch;

    public Event(String eventId, EventType eventType, EventBatch eventBatch, int bookingCapacity) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventBatch = eventBatch;
        this.bookingCapacity = bookingCapacity;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public int getBookingCapacity() {
        return bookingCapacity;
    }

    public void setBookingCapacity(int bookingCapacity) {
        this.bookingCapacity = bookingCapacity;
    }

    public int getRemainingCapacity() {
        return remainingCapacity;
    }

    public void setRemainingCapacity(int remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }

    public List<String> getCustomers() {
        return customers;
    }

    public void setCustomers(List<String> customers) {
        this.customers = customers;
    }

    public EventBatch getEventBatch() {
        return eventBatch;
    }

    public void setEventBatch(EventBatch eventBatch) {
        this.eventBatch = eventBatch;
    }
}

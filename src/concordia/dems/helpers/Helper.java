package concordia.dems.helpers;

import concordia.dems.model.enumeration.EventBatch;
import concordia.dems.model.enumeration.EventType;
import concordia.dems.model.enumeration.Servers;

public class Helper {

    /**
     * This function returns which city this client belongs to
     *
     * @param id: Customer Id or Manager Id
     * @return Servers
     */
    public static Servers getServerFromId(String id) {
        Servers server = null;
        String serverPrefix = id.substring(0, 3).toLowerCase();
        switch (serverPrefix) {
            case "mtl":
                server = Servers.MONTREAL;
                break;
            case "otw":
                server = Servers.OTTAWA;
                break;
            case "tor":
                server = Servers.TORONTO;
                break;
        }
        return server;
    }

    /**
     * Returns Enumeration Type of EventType by receiving string as parameter
     *
     * @param eventType: Type of Event
     * @return EventType
     */
    public static EventType getEventTypeEnumObject(String eventType) {
        EventType type = null;
        eventType = eventType.toUpperCase();
        switch (eventType) {
            case "CONFERENCE":
                type = EventType.CONFERENCE;
                break;
            case "SEMINAR":
                type = EventType.SEMINAR;
                break;
            case "TRADES SHOW":
                type = EventType.TRADESHOW;
                break;
            default:
                break;
        }
        return type;
    }

    /**
     * Returns Enumeration Type of EventBatch by receiving char as parameter
     *
     * @param eventBatchLetter: Batch/Time Slot of Event
     * @return EventBatch
     */
    public static EventBatch getEventBatchEnumObject(char eventBatchLetter) {
        EventBatch batch = null;
        switch (eventBatchLetter) {
            case 'A':
                batch = EventBatch.AFTERNOON;
                break;
            case 'E':
                batch = EventBatch.EVENING;
                break;
            case 'M':
                batch = EventBatch.MORNING;
                break;
            default:
                break;
        }
        return batch;
    }
}

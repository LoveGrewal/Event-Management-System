package concordia.dems.communication.impl;

import concordia.dems.business.IEventManagerBusiness;
import concordia.dems.business.impl.EventManagerBusinessMontrealImpl;
import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.Constants;
import concordia.dems.helpers.EventOperation;
import concordia.dems.model.Event;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class EventManagerCommunicationMontreal extends UnicastRemoteObject implements IEventManagerCommunication {

    private IEventManagerBusiness eventManagerBusinessMontreal;

    private final int OPERATION_INDEX = 0;
    // Event Information or Client Information
    private final int INFORMATION_INDEX = 1;

    protected EventManagerCommunicationMontreal() throws RemoteException {
        super();
        eventManagerBusinessMontreal = new EventManagerBusinessMontrealImpl();
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(Constants.MTL_RMI_PORT);
            registry.bind(Constants.MTL_RMI_URL, new EventManagerCommunicationMontreal());
            System.out.println("Montreal RMI Server Ready");
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Server is not started properly");
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param userRequest: Either Manager or Customer
     */
    @Override
    public String performOperation(String userRequest) {
        String[] unWrappingRequest = userRequest.split(",", 2);
        switch (unWrappingRequest[OPERATION_INDEX]) {
            case EventOperation.BOOK_EVENT:
                boolean status = eventManagerBusinessMontreal.bookEvent(unWrappingRequest[INFORMATION_INDEX]);
                if (status)
                    return "You are registered to the requested event";
                else
                    return "No such event ID found";
            case EventOperation.CANCEL_EVENT:
                eventManagerBusinessMontreal.cancelEvent(unWrappingRequest[INFORMATION_INDEX]);
                break;
            case EventOperation.GET_BOOKING_SCHEDULE:
                eventManagerBusinessMontreal.getBookingSchedule(unWrappingRequest[INFORMATION_INDEX]);
                break;
            case EventOperation.ADD_EVENT:
                boolean saveStatus = eventManagerBusinessMontreal.addEvent(unWrappingRequest[INFORMATION_INDEX]);
                if (saveStatus)
                    return "Your event is successfully added/updated";
                else
                    return "Your event fail to added";
            case EventOperation.REMOVE_EVENT:
                boolean removeEventStatus = eventManagerBusinessMontreal.removeEvent(unWrappingRequest[INFORMATION_INDEX]);
                if (removeEventStatus)
                    return "Event was removed successfully";
                else
                    return "Event was not removed successfully";
            case EventOperation.LIST_AVAILABILITY:
                List<Event> eventList = eventManagerBusinessMontreal.listEventAvailability(unWrappingRequest[INFORMATION_INDEX]);
                StringBuilder eventAvailabilityInformation = new StringBuilder();
                for (Event e : eventList) {
                    eventAvailabilityInformation.append(e.getEventId()).append(" ").append(e.getEventType()).append(" ").append(e.getBookingCapacity()).append(" ").append(e.getRemainingCapacity()).append("\n");
                }
                return eventAvailabilityInformation.toString();
            default:
                break;
        }
        return "";
    }
}
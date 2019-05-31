package concordia.dems.communication.impl;

import concordia.dems.business.impl.EventManagerBusinessOttawaImpl;
import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.Constants;
import concordia.dems.helpers.EventOperation;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class EventManagerCommunicationOttawa extends UnicastRemoteObject implements IEventManagerCommunication {

    private final int OPERATION_INDEX = 0;
    // Event Information or Client Information
    private final int INFORMATION_INDEX = 1;

    private EventManagerBusinessOttawaImpl eventManagerBusinessOttawa;

    protected EventManagerCommunicationOttawa() throws RemoteException {
        super();
        eventManagerBusinessOttawa = new EventManagerBusinessOttawaImpl();
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(Constants.OTW_RMI_PORT);
            registry.bind(Constants.OTW_RMI_URL, new EventManagerCommunicationOttawa());
            System.out.println("Ottawa RMI Server Ready");
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
                eventManagerBusinessOttawa.addEvent(unWrappingRequest[INFORMATION_INDEX]);
                System.err.print("You are going to book an event through ottawa server");
                break;
            case EventOperation.CANCEL_EVENT:
                System.out.println("You are going to cancel an event");
                break;
            case EventOperation.GET_BOOKING_SCHEDULE:
                System.out.println("You are going to get booking schedule");
                break;
            case EventOperation.ADD_EVENT:
                eventManagerBusinessOttawa.addEvent(unWrappingRequest[INFORMATION_INDEX]);
                System.out.println("You are going to add an event through montreal server");
                break;
            case EventOperation.REMOVE_EVENT:
                eventManagerBusinessOttawa.removeEvent(unWrappingRequest[INFORMATION_INDEX]);
                System.out.println("You are going to remove an event");
                break;
            case EventOperation.LIST_AVAILABILITY:
                eventManagerBusinessOttawa.listEventAvailability(unWrappingRequest[INFORMATION_INDEX]);
                System.out.println("You are going get an event availability");
                break;
            default:
                break;
        }
        return "";
    }
}

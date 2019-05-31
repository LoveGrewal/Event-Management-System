package concordia.dems.communication.impl;

import concordia.dems.business.impl.EventManagerBusinessTorontoImpl;
import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.Constants;
import concordia.dems.helpers.EventOperation;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class EventManagerCommunicationToronto extends UnicastRemoteObject implements IEventManagerCommunication {

    private final int OPERATION_INDEX = 0;
    // Event Information or Client Information
    private final int INFORMATION_INDEX = 1;

    private EventManagerBusinessTorontoImpl eventManagerBusinessToronto;


    protected EventManagerCommunicationToronto() throws RemoteException {
        super();
        eventManagerBusinessToronto = new EventManagerBusinessTorontoImpl();
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(Constants.TOR_RMI_PORT);
            registry.bind(Constants.TOR_RMI_URL, new EventManagerCommunicationToronto());
            System.out.println("Toronto RMI Server Ready");
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
        String[] unWrappingRequest = userRequest.split(",");
        switch (unWrappingRequest[OPERATION_INDEX]) {
            case EventOperation.BOOK_EVENT:
                eventManagerBusinessToronto.addEvent(unWrappingRequest[INFORMATION_INDEX]);
                System.err.print("You are going to book an event through toronto server");
                break;
            case EventOperation.CANCEL_EVENT:
                System.out.println("You are going to cancel an event");
                break;
            case EventOperation.GET_BOOKING_SCHEDULE:
                System.out.println("You are going to get booking schedule");
                break;
            case EventOperation.ADD_EVENT:
                eventManagerBusinessToronto.addEvent(unWrappingRequest[INFORMATION_INDEX]);
                System.out.println("You are going to add an event through montreal server");
                break;
            case EventOperation.REMOVE_EVENT:
                eventManagerBusinessToronto.removeEvent(unWrappingRequest[INFORMATION_INDEX]);
                System.out.println("You are going to remove an event");
                break;
            case EventOperation.LIST_AVAILABILITY:
                eventManagerBusinessToronto.listEventAvailability(unWrappingRequest[INFORMATION_INDEX]);
                System.out.println("You are going get an event availability");
                break;
            default:
                break;
        }
        return "";
    }
}

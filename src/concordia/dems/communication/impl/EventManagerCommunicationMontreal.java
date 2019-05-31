package concordia.dems.communication.impl;

import concordia.dems.business.impl.EventManagerBusinessMontrealImpl;
import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.Constants;
import concordia.dems.helpers.EventOperation;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class EventManagerCommunicationMontreal extends UnicastRemoteObject implements IEventManagerCommunication {

    private EventManagerBusinessMontrealImpl eventManagerBusinessMontreal;

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
        System.err.print(unWrappingRequest[OPERATION_INDEX]);
        switch (unWrappingRequest[OPERATION_INDEX]) {
            case EventOperation.BOOK_EVENT:
                eventManagerBusinessMontreal.bookEvent(unWrappingRequest[INFORMATION_INDEX]);
                break;
            case EventOperation.CANCEL_EVENT:
                break;
            case EventOperation.GET_BOOKING_SCHEDULE:
                break;
            case EventOperation.ADD_EVENT:
                eventManagerBusinessMontreal.addEvent(unWrappingRequest[INFORMATION_INDEX]);
                break;
            case EventOperation.REMOVE_EVENT:
                eventManagerBusinessMontreal.removeEvent(unWrappingRequest[INFORMATION_INDEX]);
                break;
            case EventOperation.LIST_AVAILABILITY:
                eventManagerBusinessMontreal.listEventAvailability(unWrappingRequest[INFORMATION_INDEX]);
                break;
            default:
                break;
        }
        return "";
    }
}

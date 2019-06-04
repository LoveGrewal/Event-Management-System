package concordia.dems.communication.impl;

import concordia.dems.business.IEventManagerBusiness;
import concordia.dems.business.impl.EventManagerBusinessTorontoImpl;
import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.Constants;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class EventManagerCommunicationToronto extends UnicastRemoteObject implements IEventManagerCommunication {

    private final int OPERATION_INDEX = 0;
    // Event Information or Client Information
    private final int INFORMATION_INDEX = 1;

    private IEventManagerBusiness eventManagerBusinessToronto;


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
        // Checking whether string is empty
        String verifyingRequestBody = userRequest.replaceAll(",", "");
        if (verifyingRequestBody.equals("")) {
            return "The request body is empty";
        }
        String[] unWrappingRequest = userRequest.split(",", 4);
        switch (unWrappingRequest[Constants.TO_INDEX]) {
            case "montreal":
                System.err.println("Montreal UDP Request need to initiated");
                // call montreal udp here
                break;
            case "toronto":
                return eventManagerBusinessToronto.performOperation(userRequest);
            case "ottawa":
                System.err.println("Ottawa UDP Request need to initiated");
                // call ottawa UDP here
                break;
        }
        return "";
    }
}

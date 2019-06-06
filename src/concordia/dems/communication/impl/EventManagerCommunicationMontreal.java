package concordia.dems.communication.impl;

import concordia.dems.business.IEventManagerBusiness;
import concordia.dems.business.impl.EventManagerBusinessMontrealImpl;
import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.Constants;
import concordia.dems.servers.MontrealUDPClient;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class EventManagerCommunicationMontreal extends UnicastRemoteObject implements IEventManagerCommunication {

    private IEventManagerBusiness eventManagerBusinessMontreal;
    private MontrealUDPClient montrealUDPClient;


    protected EventManagerCommunicationMontreal() throws RemoteException {
        super();
        eventManagerBusinessMontreal = new EventManagerBusinessMontrealImpl();
        montrealUDPClient = new MontrealUDPClient();
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
        // Checking whether string is empty
        String verifyingRequestBody = userRequest.replaceAll(",", "");
        if (verifyingRequestBody.equals("")) {
            return "The request body is empty";
        }
        String[] unWrappingRequest = userRequest.split(",", 4);
        switch (unWrappingRequest[Constants.TO_INDEX]) {
            case "montreal":
                return eventManagerBusinessMontreal.performOperation(userRequest);
            case "toronto":
                return montrealUDPClient.sendMessageToTorontoUDP(userRequest);
            case "ottawa":
                return montrealUDPClient.sendMessageToOttawaUDP(userRequest);
        }
        return "";
    }
}

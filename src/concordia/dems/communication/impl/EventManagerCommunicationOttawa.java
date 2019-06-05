package concordia.dems.communication.impl;

import concordia.dems.business.IEventManagerBusiness;
import concordia.dems.business.impl.EventManagerBusinessOttawaImpl;
import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.Constants;
import concordia.dems.servers.OttawaUDPClient;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author MayankJariwala
 * @version 1.0.0
 */
public class EventManagerCommunicationOttawa extends UnicastRemoteObject implements IEventManagerCommunication {

    private IEventManagerBusiness eventManagerBusinessOttawa;
    private OttawaUDPClient ottawaUDPClient;

    protected EventManagerCommunicationOttawa() throws RemoteException {
        super();
        eventManagerBusinessOttawa = new EventManagerBusinessOttawaImpl();
        ottawaUDPClient = new OttawaUDPClient();
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
        // Checking whether string is empty
        String verifyingRequestBody = userRequest.replaceAll(",", "");
        if (verifyingRequestBody.equals("")) {
            return "The request body is empty";
        }
        String[] unWrappingRequest = userRequest.split(",", 4);
        switch (unWrappingRequest[Constants.TO_INDEX]) {
            case "montreal":
                //System.err.println("Montreal UDP Request need to initiated");
                // call montreal udp here
                return ottawaUDPClient.sendMessageToMontrealUDP(userRequest);
            case "toronto":
                //System.err.println("Toronto UDP Request need to initiated");
                // call toronto UDP here
                return ottawaUDPClient.sendMessageToTorontoUDP(userRequest);
            case "ottawa":
                return eventManagerBusinessOttawa.performOperation(userRequest);
        }
        return "";
    }
}

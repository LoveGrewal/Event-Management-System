package concordia.dems.communication.impl;

import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.Constants;
import concordia.dems.helpers.EventOperation;
import concordia.dems.helpers.Helper;
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

    private OttawaUDPClient ottawaUDPClient;

    protected EventManagerCommunicationOttawa() throws RemoteException {
        super();
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

        // If request is for event availability then simply returns all events in server
        if (unWrappingRequest[Constants.ACTION_INDEX].equals(EventOperation.LIST_AVAILABILITY)) {
            return getEventAvailabilityFromAllServers(userRequest);
        }

        // For getting booking schedule of user , also call all servers service
        if (unWrappingRequest[Constants.ACTION_INDEX].equals(EventOperation.GET_BOOKING_SCHEDULE)) {
            return getBookingScheduleForClients(userRequest);
        }

        switch (unWrappingRequest[Constants.TO_INDEX]) {
            case "montreal":
                if (unWrappingRequest[Constants.ACTION_INDEX].equals(EventOperation.BOOK_EVENT)) {
                    unWrappingRequest[Constants.ACTION_INDEX] = EventOperation.GET_BOOKING_SCHEDULE;
                    boolean isNotEligible = isCustomerEligibleForBookingEvent(unWrappingRequest);
                    if (isNotEligible)
                        return "Limit Exceeded! You have been already registered for 3 events for a specific month";
                    return ottawaUDPClient.sendMessageToMontrealUDP(userRequest);
                } else
                    return ottawaUDPClient.sendMessageToMontrealUDP(userRequest);
            case "toronto":
                if (unWrappingRequest[Constants.ACTION_INDEX].equals(EventOperation.BOOK_EVENT)) {
                    unWrappingRequest[Constants.ACTION_INDEX] = EventOperation.GET_BOOKING_SCHEDULE;
                    boolean isNotEligible = isCustomerEligibleForBookingEvent(unWrappingRequest);
                    if (isNotEligible)
                        return "Limit Exceeded! You have been already registered for 3 events for a specific month";
                    return ottawaUDPClient.sendMessageToTorontoUDP(userRequest);
                } else
                    return ottawaUDPClient.sendMessageToTorontoUDP(userRequest);
            case "ottawa":
                return ottawaUDPClient.sendMessageToOttawaUDP(userRequest);

        }
        return "";
    }


    private String generateStringForUnwrappingRequest(String[] unWrappingRequest) {
        return String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]);
    }

    private String getEventAvailabilityFromAllServers(String userRequest) {
        String torontoEvents = ottawaUDPClient.sendMessageToTorontoUDP(userRequest);
        String ottawaEvents = ottawaUDPClient.sendMessageToOttawaUDP(userRequest);
        String montrealEvents = ottawaUDPClient.sendMessageToMontrealUDP(userRequest);
        return String.join("\n", torontoEvents, ottawaEvents, montrealEvents);
    }

    private String getBookingScheduleForClients(String userRequest) {
        String torontoEvents = ottawaUDPClient.sendMessageToTorontoUDP(userRequest);
        String ottawaEvents = ottawaUDPClient.sendMessageToOttawaUDP(userRequest);
        String montrealEvents = ottawaUDPClient.sendMessageToMontrealUDP(userRequest);
        return String.join("\n", torontoEvents, ottawaEvents, montrealEvents);
    }

    private boolean isCustomerEligibleForBookingEvent(String[] unWrappingRequest) {
        String torontoEvents = ottawaUDPClient.sendMessageToTorontoUDP(generateStringForUnwrappingRequest(unWrappingRequest));
        String montrealEvents = ottawaUDPClient.sendMessageToMontrealUDP(generateStringForUnwrappingRequest(unWrappingRequest));
        return Helper.checkIfEqualMoreThanThree(torontoEvents, montrealEvents, unWrappingRequest[Constants.INFORMATION_INDEX]);
    }
}

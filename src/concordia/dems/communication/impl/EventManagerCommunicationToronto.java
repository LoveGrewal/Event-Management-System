package concordia.dems.communication.impl;

import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.Constants;
import concordia.dems.helpers.EventOperation;
import concordia.dems.helpers.Helper;
import concordia.dems.servers.TorontoUDPClient;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class EventManagerCommunicationToronto extends UnicastRemoteObject implements IEventManagerCommunication {

    private TorontoUDPClient torontoUDPClient;


    protected EventManagerCommunicationToronto() throws RemoteException {
        super();
        torontoUDPClient = new TorontoUDPClient();
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
                    return torontoUDPClient.sendMessageToMontrealUDP(userRequest);
                } else
                    return torontoUDPClient.sendMessageToMontrealUDP(userRequest);

            case "toronto":
                return torontoUDPClient.sendMessageToTorontoUDP(userRequest);

            case "ottawa":
                if (unWrappingRequest[Constants.ACTION_INDEX].equals(EventOperation.BOOK_EVENT)) {
                    unWrappingRequest[Constants.ACTION_INDEX] = EventOperation.GET_BOOKING_SCHEDULE;
                    boolean isNotEligible = isCustomerEligibleForBookingEvent(unWrappingRequest);
                    if (isNotEligible)
                        return "Limit Exceeded! You have been already registered for 3 events for a specific month";
                    return torontoUDPClient.sendMessageToOttawaUDP(userRequest);
                } else
                    return torontoUDPClient.sendMessageToOttawaUDP(userRequest);
        }
        return "";
    }

    private String generateStringForUnwrappingRequest(String[] unWrappingRequest) {
        return String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]);
    }

    private String getEventAvailabilityFromAllServers(String userRequest) {
        String torontoEvents = torontoUDPClient.sendMessageToTorontoUDP(userRequest);
        String ottawaEvents = torontoUDPClient.sendMessageToOttawaUDP(userRequest);
        String montrealEvents = torontoUDPClient.sendMessageToMontrealUDP(userRequest);
        return String.join("\n", torontoEvents, ottawaEvents, montrealEvents);
    }

    private String getBookingScheduleForClients(String userRequest) {
        String torontoEvents = torontoUDPClient.sendMessageToTorontoUDP(userRequest);
        String ottawaEvents = torontoUDPClient.sendMessageToOttawaUDP(userRequest);
        String montrealEvents = torontoUDPClient.sendMessageToMontrealUDP(userRequest);
        return String.join("\n", torontoEvents, ottawaEvents, montrealEvents);
    }

    private boolean isCustomerEligibleForBookingEvent(String[] unWrappingRequest) {
        String ottawaEvents = torontoUDPClient.sendMessageToOttawaUDP(generateStringForUnwrappingRequest(unWrappingRequest));
        String montrealEvents = torontoUDPClient.sendMessageToMontrealUDP(generateStringForUnwrappingRequest(unWrappingRequest));
        return Helper.checkIfEqualMoreThanThree(ottawaEvents, montrealEvents, unWrappingRequest[Constants.INFORMATION_INDEX]);
    }
}

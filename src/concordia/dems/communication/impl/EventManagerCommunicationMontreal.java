package concordia.dems.communication.impl;

import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.Constants;
import concordia.dems.helpers.EventOperation;
import concordia.dems.helpers.Helper;
import concordia.dems.servers.MontrealUDPClient;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class EventManagerCommunicationMontreal extends UnicastRemoteObject implements IEventManagerCommunication {

    private MontrealUDPClient montrealUDPClient;


    protected EventManagerCommunicationMontreal() throws RemoteException {
        super();
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
     * The one which is responsible to call business layer of montreal or to call
     * an UDP of other server
     *
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
                return montrealUDPClient.sendMessageToMontrealUDP(userRequest);
            case "toronto":
                if (unWrappingRequest[Constants.ACTION_INDEX].equals(EventOperation.BOOK_EVENT)) {
                    unWrappingRequest[Constants.ACTION_INDEX] = EventOperation.GET_BOOKING_SCHEDULE;
                    boolean isNotEligible = isCustomerEligibleForBookingEvent(unWrappingRequest);
                    if (isNotEligible)
                        return "Limit Exceeded! You have been already registered for 3 events for a specific month";
                    return montrealUDPClient.sendMessageToTorontoUDP(userRequest);
                } else
                    return montrealUDPClient.sendMessageToTorontoUDP(userRequest);
            case "ottawa":
                if (unWrappingRequest[Constants.ACTION_INDEX].equals(EventOperation.BOOK_EVENT)) {
                    unWrappingRequest[Constants.ACTION_INDEX] = EventOperation.GET_BOOKING_SCHEDULE;
                    boolean isNotEligible = isCustomerEligibleForBookingEvent(unWrappingRequest);
                    if (isNotEligible)
                        return "Limit Exceeded! You have been already registered for 3 events for a specific month";
                    return montrealUDPClient.sendMessageToOttawaUDP(userRequest);
                } else
                    return montrealUDPClient.sendMessageToOttawaUDP(userRequest);
        }
        return "";
    }


    private String generateStringForUnwrappingRequest(String[] unWrappingRequest) {
        return String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]);
    }

    private String getEventAvailabilityFromAllServers(String userRequest) {
        String torontoEvents = montrealUDPClient.sendMessageToTorontoUDP(userRequest);
        String ottawaEvents = montrealUDPClient.sendMessageToOttawaUDP(userRequest);
        String montrealEvents = montrealUDPClient.sendMessageToMontrealUDP(userRequest);
        return String.join("\n", torontoEvents, ottawaEvents, montrealEvents);
    }

    private String getBookingScheduleForClients(String userRequest) {
        String torontoEvents = montrealUDPClient.sendMessageToTorontoUDP(userRequest);
        String ottawaEvents = montrealUDPClient.sendMessageToOttawaUDP(userRequest);
        String montrealEvents = montrealUDPClient.sendMessageToMontrealUDP(userRequest);
        return String.join("\n", torontoEvents, ottawaEvents, montrealEvents);
    }

    private boolean isCustomerEligibleForBookingEvent(String[] unWrappingRequest) {
        String torontoEvents = montrealUDPClient.sendMessageToTorontoUDP(generateStringForUnwrappingRequest(unWrappingRequest));
        String ottawaEvents = montrealUDPClient.sendMessageToOttawaUDP(generateStringForUnwrappingRequest(unWrappingRequest));
        return Helper.checkIfEqualMoreThanThree(torontoEvents, ottawaEvents, unWrappingRequest[Constants.INFORMATION_INDEX]);
    }
}

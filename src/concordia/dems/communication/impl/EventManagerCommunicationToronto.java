package concordia.dems.communication.impl;

import concordia.dems.business.IEventManagerBusiness;
import concordia.dems.business.impl.EventManagerBusinessTorontoImpl;
import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.Constants;
import concordia.dems.helpers.EventOperation;
import concordia.dems.servers.TorontoUDPClient;

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
    private TorontoUDPClient torontoUDPClient;


    protected EventManagerCommunicationToronto() throws RemoteException {
        super();
        eventManagerBusinessToronto = new EventManagerBusinessTorontoImpl();
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
        switch (unWrappingRequest[Constants.TO_INDEX]) {
            case "montreal":
                //System.err.println("Montreal UDP Request need to initiated");
                // call montreal udp here
                if (unWrappingRequest[Constants.ACTION_INDEX].equalsIgnoreCase(EventOperation.BOOK_EVENT)) {
                    //fetch list of event on toronto server
                    unWrappingRequest[Constants.ACTION_INDEX] = EventOperation.GET_BOOKING_SCHEDULE;

                    String ottawaEvents = torontoUDPClient.sendMessageToOttawaUDP(String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]));

                    //fetch list of event on ottawa server
                    String montrealEvents = torontoUDPClient.sendMessageToMontrealUDP(String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]));
                    if (checkIfEqualMoreThanThree(ottawaEvents, montrealEvents, unWrappingRequest[Constants.INFORMATION_INDEX])) {
                        return "Limit Exceeded";
                    } else {
                        return torontoUDPClient.sendMessageToMontrealUDP(userRequest);
                    }
                } else {
                    return torontoUDPClient.sendMessageToMontrealUDP(userRequest);
                }

            case "toronto":
                if (unWrappingRequest[Constants.ACTION_INDEX].equalsIgnoreCase(EventOperation.GET_BOOKING_SCHEDULE)) {
                    //fetch list of event on toronto server


                    String ottawaEvents = torontoUDPClient.sendMessageToOttawaUDP(String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]));

                    //fetch list of event on ottawa server
                    String montrealEvents = torontoUDPClient.sendMessageToMontrealUDP(String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]));
                    String torontoEvents = eventManagerBusinessToronto.performOperation(userRequest);
                    return String.join("\n", torontoEvents, ottawaEvents, montrealEvents);
                } else if (unWrappingRequest[Constants.ACTION_INDEX].equalsIgnoreCase(EventOperation.LIST_AVAILABILITY)) {
                    String ottawaEvents = torontoUDPClient.sendMessageToOttawaUDP(userRequest);
                    String montrealEvents = torontoUDPClient.sendMessageToMontrealUDP(userRequest);
                    String torontoEvents = eventManagerBusinessToronto.performOperation(userRequest);
                    return String.join("\n", torontoEvents, ottawaEvents, montrealEvents);
                } else {
                    return eventManagerBusinessToronto.performOperation(userRequest);
                }

            case "ottawa":
                //System.err.println("Ottawa UDP Request need to initiated");
                // call ottawa UDP here
                if (unWrappingRequest[Constants.ACTION_INDEX].equalsIgnoreCase(EventOperation.BOOK_EVENT)) {
                    //fetch list of event on toronto server
                    unWrappingRequest[Constants.ACTION_INDEX] = EventOperation.GET_BOOKING_SCHEDULE;

                    String ottawaEvents = torontoUDPClient.sendMessageToOttawaUDP(String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]));

                    //fetch list of event on ottawa server
                    String montrealEvents = torontoUDPClient.sendMessageToMontrealUDP(String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]));
                    if (checkIfEqualMoreThanThree(ottawaEvents, montrealEvents, unWrappingRequest[Constants.INFORMATION_INDEX])) {
                        return "Limit Exceeded";
                    } else {
                        return torontoUDPClient.sendMessageToOttawaUDP(userRequest);
                    }
                } else {
                    return torontoUDPClient.sendMessageToOttawaUDP(userRequest);
                }

        }
        return "";
    }

    private boolean checkIfEqualMoreThanThree(String events1, String events2, String inf) {
        //get month of current booking
        String currMonth = inf.split(",")[1].substring(6, 8).trim();
        int eventCount = 0;

        String[] events = events1.split("\n");
        for (String s : events) {
            if (currMonth.equalsIgnoreCase(s.split(" ")[0].substring(6, 8).trim())) {
                eventCount++;
            }
        }
        events = events2.split("\n");
        for (String s : events) {
            if (currMonth.equalsIgnoreCase(s.split(" ")[0].substring(6, 8).trim())) {
                eventCount++;
            }
        }
        return (eventCount >= 3);
    }
}

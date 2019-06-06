package concordia.dems.communication.impl;

import concordia.dems.business.IEventManagerBusiness;
import concordia.dems.business.impl.EventManagerBusinessMontrealImpl;
import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.Constants;
import concordia.dems.helpers.EventOperation;
import concordia.dems.servers.MontrealUDPClient;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

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
                if (unWrappingRequest[Constants.ACTION_INDEX].equalsIgnoreCase(EventOperation.GET_BOOKING_SCHEDULE)) {
                    //fetch list of event on toronto server


                    String torontoEvents = montrealUDPClient.sendMessageToTorontoUDP(String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]));

                    //fetch list of event on ottawa server
                    String ottawaEvents = montrealUDPClient.sendMessageToOttawaUDP(String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]));
                    String montrealEvents = eventManagerBusinessMontreal.performOperation(userRequest);
                    return String.join("\n", torontoEvents, ottawaEvents, montrealEvents);
                } else if (unWrappingRequest[Constants.ACTION_INDEX].equalsIgnoreCase(EventOperation.LIST_AVAILABILITY)) {
                    String torontoEvents = montrealUDPClient.sendMessageToTorontoUDP(userRequest);
                    String ottawaEvents = montrealUDPClient.sendMessageToOttawaUDP(userRequest);
                    String montrealEvents = eventManagerBusinessMontreal.performOperation(userRequest);
                    return String.join("\n", torontoEvents, ottawaEvents, montrealEvents);
                } else {
                    return eventManagerBusinessMontreal.performOperation(userRequest);
                }

            case "toronto":
                if (unWrappingRequest[Constants.ACTION_INDEX].equalsIgnoreCase(EventOperation.BOOK_EVENT)) {
                    //fetch list of event on toronto server
                    unWrappingRequest[Constants.ACTION_INDEX] = EventOperation.GET_BOOKING_SCHEDULE;

                    String torontoEvents = montrealUDPClient.sendMessageToTorontoUDP(String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]));

                    //fetch list of event on ottawa server
                    String ottawaEvents = montrealUDPClient.sendMessageToOttawaUDP(String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]));
                    if (checkIfEqualMoreThanThree(torontoEvents, ottawaEvents, unWrappingRequest[Constants.INFORMATION_INDEX])) {
                        return "Limit Exceeded";
                    } else {
                        return montrealUDPClient.sendMessageToTorontoUDP(userRequest);
                    }
                } else {
                    return montrealUDPClient.sendMessageToTorontoUDP(userRequest);
                }
            case "ottawa":
                if (unWrappingRequest[Constants.ACTION_INDEX].equalsIgnoreCase(EventOperation.BOOK_EVENT)) {
                    //fetch list of event on toronto server
                    unWrappingRequest[Constants.ACTION_INDEX] = EventOperation.GET_BOOKING_SCHEDULE;

                    String torontoEvents = montrealUDPClient.sendMessageToTorontoUDP(String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]));

                    //fetch list of event on ottawa server
                    String ottawaEvents = montrealUDPClient.sendMessageToOttawaUDP(String.join(",", unWrappingRequest[0], unWrappingRequest[1], unWrappingRequest[2], unWrappingRequest[3]));
                    if (checkIfEqualMoreThanThree(torontoEvents, ottawaEvents, unWrappingRequest[Constants.INFORMATION_INDEX])) {
                        return "Limit Exceeded";
                    } else {
                        return montrealUDPClient.sendMessageToOttawaUDP(userRequest);
                    }
                } else {
                    return montrealUDPClient.sendMessageToOttawaUDP(userRequest);
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

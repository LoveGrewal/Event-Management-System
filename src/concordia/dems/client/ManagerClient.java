package concordia.dems.client;

import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.EventOperation;
import concordia.dems.helpers.Helper;
import concordia.dems.helpers.ManagerAndClientInfo;
import concordia.dems.model.RMIServerFactory;
import concordia.dems.model.enumeration.EventType;
import concordia.dems.model.enumeration.Servers;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mayank Jariwala
 */
public class ManagerClient {

    private IEventManagerCommunication iEventManagerCommunication;
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ManagerClient managerClient = new ManagerClient();
        managerClient.execute();
    }

    private void execute() {
        String customerId, eventInfo, response, requestBody;
        System.err.print("Enter your id : ");
        String managerID = scanner.nextLine();
        Servers servers = Helper.getServerFromId(managerID);
        iEventManagerCommunication = RMIServerFactory.getInstance(servers);
        while (true) {
            showManagerOperations();
            int operationID = scanner.nextInt();
            String operationName = ManagerAndClientInfo.managerOperations.get(operationID - 1);
            try {
                switch (operationName) {
                    case EventOperation.ADD_EVENT:
                        String eventInformation = createNewEvent();
                        requestBody = EventOperation.ADD_EVENT + "," + eventInformation;
                        response = iEventManagerCommunication.performOperation(requestBody);
                        System.err.println(response);
                        break;
                    case EventOperation.REMOVE_EVENT:
                        requestBody = EventOperation.REMOVE_EVENT + "," + removeEventInformation();
                        response = iEventManagerCommunication.performOperation(requestBody);
                        System.err.println(response);
                        break;
                    case EventOperation.LIST_AVAILABILITY:
                        System.err.print("Enter event type: ");
                        eventInfo = scanner.next();
                        requestBody = EventOperation.LIST_AVAILABILITY + "," + eventInfo;
                        String listEventResponse = iEventManagerCommunication.performOperation(requestBody);
                        System.err.println(listEventResponse);
                        break;
                    // Manager can perform operation for client
                    case EventOperation.BOOK_EVENT:
                        requestBody = EventOperation.BOOK_EVENT + "," + bookEventInformation();
                        response = iEventManagerCommunication.performOperation(requestBody);
                        System.err.print(response);
                        break;
                    case EventOperation.CANCEL_EVENT:
                        requestBody = EventOperation.CANCEL_EVENT + "," + cancelEventInformation();
                        response = iEventManagerCommunication.performOperation(requestBody);
                        System.err.print(response);
                        break;
                    case EventOperation.GET_BOOKING_SCHEDULE:
                        System.out.print("Enter your client ID : ");
                        customerId = scanner.next();
                        requestBody = EventOperation.GET_BOOKING_SCHEDULE + "," + customerId;
                        response = iEventManagerCommunication.performOperation(requestBody);
                        System.err.print(response);
                        break;
                }
            } catch (RemoteException e) {
                System.err.print("Exception message " + e.getMessage());
            }
        }
    }

    /**
     * Just Listing dummy values of Manager
     */
    private void listManagerIds() {
        AtomicInteger idCounter = new AtomicInteger(1);
        System.err.println("Select number from below option for identifying manager");
        ManagerAndClientInfo.managersId.forEach(managerId -> System.out.println(idCounter.getAndIncrement() + " " + managerId));
    }

    private void showManagerOperations() {
        AtomicInteger idCounter = new AtomicInteger(1);
        System.err.println("Select operation id from below option : ");
        ManagerAndClientInfo.managerOperations.forEach(managerOperation -> System.out.println(idCounter.getAndIncrement() + " " + managerOperation));
    }

    /**
     * Ask question regarding new event from manager
     *
     * @return String
     */
    private String createNewEvent() {
        String newEventInfo;
        System.err.print("Enter event City (MTL,TOR,OTW) : ");
        String eventCity = scanner.next().toUpperCase();
        System.err.print("Enter event Batch (A/M/E): ");
        String eventBatch = scanner.next().toUpperCase();
        System.err.print("Enter event date (ddmmyy): ");
        String eventDate = scanner.next().toUpperCase();
        String eventID = eventCity + "" + eventBatch + "" + eventDate;
        System.err.println("Enter event Type from below list : ");
        Arrays.stream(EventType.values()).forEach(System.out::println);
        String eventType = scanner.next();
        System.err.print("Enter event booking capacity : ");
        int bookingCapacity = scanner.nextInt();
        System.out.println("Generated Event ID is : " + eventID);
        newEventInfo = eventID + "," + eventType + "," + eventBatch + "," + bookingCapacity;
        return newEventInfo;
    }

    /**
     * Ask Certain Information While Booking an Event from User or Manager
     *
     * @return String : Request Body
     */
    private String bookEventInformation() {
        String requestBody = "";
        System.err.print("Enter Customer ID : ");
        requestBody += scanner.next();
        System.err.print("Enter EVENT ID : ");
        requestBody += "," + scanner.next();
        System.err.print("Enter EVENT Type(SEMINAR/CONFERENCE/TRADESHOW) : ");
        requestBody += "," + scanner.next();
        return requestBody;
    }

    private String removeEventInformation() {
        String removeEventInfo = "";
        System.err.print("Enter event id: ");
        removeEventInfo += scanner.next();
        System.err.print("Enter event type: ");
        removeEventInfo += "," + scanner.next().toUpperCase();
        return removeEventInfo;
    }

    private String cancelEventInformation() {
        String body = "";
        System.out.print("Enter your client ID : ");
        body += scanner.next();
        System.out.print(" Enter Event ID : ");
        body += "," + scanner.next();
        return body;
    }
}

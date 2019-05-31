package concordia.dems.client;

import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.EventOperation;
import concordia.dems.helpers.Helper;
import concordia.dems.helpers.ManagerAndClientInfo;
import concordia.dems.model.Event;
import concordia.dems.model.RMIServerFactory;
import concordia.dems.model.enumeration.EventBatch;
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

    public static void main(String[] args) {
        ManagerClient managerClient = new ManagerClient();
        managerClient.execute();
    }

    private void execute() {
        String requestBody = "", customerId, eventInfo, response;
        System.err.print("Enter your id : ");
        Scanner scanner = new Scanner(System.in);
        String managerID = scanner.nextLine();
        Servers servers = Helper.getServerFromId(managerID);
        iEventManagerCommunication = RMIServerFactory.getInstance(servers);
        showManagerOperations();
        int operationID = scanner.nextInt();
        String operationName = ManagerAndClientInfo.managerOperations.get(operationID - 1);
        try {
            switch (operationName) {
                case EventOperation.ADD_EVENT:
                    String eventInformation = createNewEvent();
                    requestBody += EventOperation.ADD_EVENT + "," + eventInformation;
                    response = iEventManagerCommunication.performOperation(requestBody);
                    System.err.print(response);
                    break;
                case EventOperation.REMOVE_EVENT:
                    System.err.print("Enter event id: ");
                    eventInfo = scanner.nextLine();
                    requestBody += EventOperation.REMOVE_EVENT + "," + eventInfo;
                    response = iEventManagerCommunication.performOperation(requestBody);
                    System.err.print(response);
                    break;
                case EventOperation.LIST_AVAILABILITY:
                    System.err.print("Enter event type: ");
                    eventInfo = scanner.nextLine();
                    requestBody += EventOperation.LIST_AVAILABILITY + "," + eventInfo;
                    response = iEventManagerCommunication.performOperation(requestBody);
                    System.err.print(response);
                    break;
                // Manager can perform operation for client
                case EventOperation.BOOK_EVENT:
                    System.out.print("Enter your client ID : ");
                    customerId = scanner.nextLine();
                    requestBody += EventOperation.BOOK_EVENT + "," + customerId;
                    break;
                case EventOperation.CANCEL_EVENT:
                    System.out.print("Enter your client ID : ");
                    customerId = scanner.nextLine();
                    requestBody += EventOperation.CANCEL_EVENT + "," + customerId;
                    break;
                case EventOperation.GET_BOOKING_SCHEDULE:
                    System.out.print("Enter your client ID : ");
                    customerId = scanner.nextLine();
                    requestBody += EventOperation.GET_BOOKING_SCHEDULE + "," + customerId;
                    break;
            }
        } catch (RemoteException e) {
            System.err.print("Exception message " + e.getMessage());
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
        System.err.print("Enter event ID : ");
        Scanner scanner = new Scanner(System.in);
        String eventID = scanner.nextLine();
        System.err.println("Enter event Type from below list : ");
        Arrays.stream(EventType.values()).forEach(System.out::println);
        String eventType = scanner.nextLine();
        System.err.println("Enter event Batch from below list : ");
        Arrays.stream(EventBatch.values()).forEach(System.out::println);
        String eventBatch = scanner.nextLine();
        System.err.println("Enter event booking capacity : ");
        int bookingCapacity = scanner.nextInt();
        newEventInfo = eventID + "," + eventType + "," + eventBatch + "," + bookingCapacity;
        return newEventInfo;
    }
}

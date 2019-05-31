package concordia.dems.client;

import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.EventOperation;
import concordia.dems.helpers.Helper;
import concordia.dems.helpers.ManagerAndClientInfo;
import concordia.dems.model.Event;
import concordia.dems.model.RMIServerFactory;
import concordia.dems.model.enumeration.EventType;
import concordia.dems.model.enumeration.Servers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mayank Jariwala
 * @version 1.0.0
 */
public class CustomerClient {

    public static void main(String[] args) {
        CustomerClient customerClient = new CustomerClient();
        System.out.println("Please write logout to close the client server");
        customerClient.execute();
    }

    private void execute() {
        Scanner readInput = new Scanner(System.in);
        String customerId;
        System.out.print("Enter your id : ");
        customerId = readInput.nextLine();
        while (!customerId.equals("logout")) {
            try {
                Servers server = Helper.getServerFromId(customerId);
                IEventManagerCommunication communication = RMIServerFactory.getInstance(server);
                String requestBody = "";
                listCustomerOperations();
                System.out.print("Select operation id : ");
                int operationID = readInput.nextInt();
                String operationName = ManagerAndClientInfo.clientOperations.get(operationID - 1);
                Event chosenEvent = null;
                switch (operationName) {
                    case EventOperation.BOOK_EVENT:
                        AtomicInteger idCounter = new AtomicInteger(1);
                        List<Event> eventsList = createEventsAndList();
                        eventsList.forEach(event -> System.out.println(idCounter.getAndIncrement() + " " + event.getEventId() + " " + event.getEventType()));
                        System.out.print("Select event: ");
                        int eventID = readInput.nextInt();
                        chosenEvent = eventsList.get(eventID - 1);
                        requestBody += EventOperation.BOOK_EVENT;
                        break;
                    case EventOperation.CANCEL_EVENT:

                        break;
                    case EventOperation.GET_BOOKING_SCHEDULE:
                        break;
                }
                requestBody += "," + customerId + "," + (chosenEvent != null ? chosenEvent.getEventId() : null) + "," + (chosenEvent != null ? chosenEvent.getEventType() : null);
                String response = communication.performOperation(requestBody);
                System.out.println(response);
                readInput.nextLine();
                System.out.print("Enter your id : ");
                customerId = readInput.nextLine();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Just Listing dummy values of Manager
     */
    private void listCustomerIds() {
        AtomicInteger idCounter = new AtomicInteger(1);
        System.out.println("Select number from below option for identifying customer");
        ManagerAndClientInfo.clientsId.forEach(clientId -> System.out.println(idCounter.getAndIncrement() + " " + clientId));
    }

    /**
     * Just Listing dummy values of Manager
     */
    private void listCustomerOperations() {
        AtomicInteger idCounter = new AtomicInteger(1);
        System.out.println("Select number from below option for performing any operation");
        ManagerAndClientInfo.clientOperations.forEach(clientId -> System.out.println(idCounter.getAndIncrement() + " " + clientId));
    }

    // This function is just for testing
    private List<Event> createEventsAndList() {
        List<Event> eventsList = new ArrayList<>();
        eventsList.add(new Event("MTLE130519", EventType.CONFERENCE));
        eventsList.add(new Event("OTWA060519", EventType.SEMINAR));
        eventsList.add(new Event("TORM180519", EventType.TRADESHOW));
        eventsList.add(new Event("MTLE190519", EventType.CONFERENCE));
        return eventsList;
    }
}

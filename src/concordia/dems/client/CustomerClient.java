package concordia.dems.client;

import concordia.dems.helpers.EventOperation;
import concordia.dems.helpers.Helper;
import concordia.dems.helpers.ManagerAndClientInfo;
import concordia.dems.model.Event;
import concordia.dems.model.enumeration.EventType;
import concordia.dems.model.enumeration.Servers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mayank Jariwala
 */
public class CustomerClient {


    public static void main(String[] args) {
        CustomerClient customerClient = new CustomerClient();
        customerClient.execute();
    }

    private void execute() {
        String requestBody = "";
        listCustomerIds();
        System.out.print("Select your id : ");
        Scanner scanner = new Scanner(System.in);
        int customerID = scanner.nextInt();
        Servers server = Helper.getServerFromId(ManagerAndClientInfo.clientsId.get(customerID - 1));
        listCustomerOperations();
        System.out.print("Select operation id : ");
        int operationID = scanner.nextInt();
        String operationName = ManagerAndClientInfo.clientOperations.get(operationID - 1);
        if (operationName.equals(EventOperation.BOOK_EVENT)) {
            AtomicInteger idCounter = new AtomicInteger(1);
            List<Event> eventsList = createEventsAndList();
            eventsList.forEach(event -> {
                System.out.println(idCounter.getAndIncrement() + " " + event);
            });
        }
        requestBody += operationName + "," + customerID;
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

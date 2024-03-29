package concordia.dems.client;

import concordia.dems.communication.IEventManagerCommunication;
import concordia.dems.helpers.*;
import concordia.dems.model.Event;
import concordia.dems.model.RMIServerFactory;
import concordia.dems.model.enumeration.Servers;

import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mayank Jariwala
 * @version 1.0.0
 */
public class CustomerClient {

    private Scanner readInput = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to customer application :)");
        CustomerClient customerClient = new CustomerClient();
        customerClient.execute();
    }

    private void execute() {
        String customerId;
        System.out.print("Enter your id : ");
        customerId = readInput.nextLine();
        String from = Helper.getServerNameFromID(customerId);
        Servers server = Helper.getServerFromId(customerId);
        IEventManagerCommunication communication = RMIServerFactory.getInstance(server);
        String requestBody;
        while (true) {
            try {
                listCustomerOperations();
                System.out.print("Select operation id : ");
                int operationID = readInput.nextInt();
                String operationName = ManagerAndClientInfo.clientOperations.get(operationID - 1);
                switch (operationName) {
                    case EventOperation.BOOK_EVENT:
                        requestBody = from + "," + this.bookEventInformation(customerId);
                        String bookEventResponse = communication.performOperation(requestBody);
                        System.out.println(bookEventResponse);
                        Logger.writeLogToFile("client", customerId, requestBody, bookEventResponse, Constants.TIME_STAMP);
                        break;
                    case EventOperation.CANCEL_EVENT:
                        requestBody = from + "," + this.cancelEventInformation(customerId);
                        String cancelEventResponse = communication.performOperation(requestBody);
                        System.out.println(cancelEventResponse);
                        Logger.writeLogToFile("client", customerId, requestBody, cancelEventResponse, Constants.TIME_STAMP);
                        break;
                    case EventOperation.GET_BOOKING_SCHEDULE:
                        requestBody = from + "," + from + "," + EventOperation.GET_BOOKING_SCHEDULE + "," + customerId;
                        String bookingScheduleResponse = communication.performOperation(requestBody);
                        System.out.println(bookingScheduleResponse);
                        Logger.writeLogToFile("client", customerId, requestBody, bookingScheduleResponse, Constants.TIME_STAMP);
                        break;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

    }

    /**
     * Ask Certain Information While Booking an Event from User or Manager
     *
     * @return String : Request Body
     */
    private String bookEventInformation(String customerID) {
        String requestBody = customerID;
        System.err.print("Enter EVENT ID : ");
        String eventID = readInput.next();
        String to = Helper.getServerNameFromID(eventID);
        requestBody += "," + eventID;
        System.err.print("Enter EVENT Type(SEMINAR/CONFERENCE/TRADESHOW) : ");
        requestBody += "," + readInput.next();
        return to + "," + EventOperation.BOOK_EVENT + "," + requestBody;
    }

    private String cancelEventInformation(String customerId) {
        String body = customerId;
        System.out.print("Enter Event ID : ");
        String eventID = readInput.next();
        body += "," + eventID;
        String to = Helper.getServerNameFromID(eventID);
        return to + "," + EventOperation.CANCEL_EVENT + "," + body;
    }

    private void listCustomerOperations() {
        AtomicInteger idCounter = new AtomicInteger(1);
        System.out.println("Select number from below option for performing any operation");
        ManagerAndClientInfo.clientOperations.forEach(clientId -> System.out.println(idCounter.getAndIncrement() + " " + clientId));
    }
}

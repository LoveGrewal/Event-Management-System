package concordia.dems.client;

import concordia.dems.helpers.ManagerAndClientInfo;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mayank Jariwala
 */
public class ManagerClient {

    public static void main(String[] args) {
        ManagerClient managerClient = new ManagerClient();
        managerClient.listManagerIds();
        System.out.print("Select your id : ");
        Scanner scanner = new Scanner(System.in);
        int selectedID = scanner.nextInt();
        System.out.println(ManagerAndClientInfo.managersId.get(selectedID - 1));
    }

    /**
     * Just Listing dummy values of Manager
     */
    private void listManagerIds() {
        AtomicInteger idCounter = new AtomicInteger(1);
        System.out.println("Select number from below option for identifying manager");
        ManagerAndClientInfo.managersId.forEach(managerId -> System.out.println(idCounter.getAndIncrement() + " " + managerId));
    }
}

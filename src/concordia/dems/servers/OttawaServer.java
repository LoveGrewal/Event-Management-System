package concordia.dems.servers;

import concordia.dems.helpers.Constants;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class OttawaServer {

    public static void main(String[] args) {
        OttawaServer ottawaServer = new OttawaServer();
        ottawaServer.initConnection();
    }

    private void initConnection() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            InetAddress ipAddress = InetAddress.getLocalHost();
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String readMessage = scanner.nextLine();
                byte[] byteMessage = readMessage.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(byteMessage, byteMessage.length, ipAddress, Constants.OTW_MTL_PORT);
                datagramSocket.send(datagramPacket);
                if (readMessage.equals("bye"))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

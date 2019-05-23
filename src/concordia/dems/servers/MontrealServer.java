package concordia.dems.servers;

import concordia.dems.helpers.Constants;

import java.io.IOException;
import java.net.*;

public class MontrealServer {

    private DatagramSocket montrealSocket = null, torontoSocket = null, ottawaSocket = null;

    public static void main(String[] args) {
        MontrealServer montrealServer = new MontrealServer();
        montrealServer.initConnection();
    }

    private void initConnection() {
        try {
            InetAddress ipAddress = InetAddress.getLocalHost();
            byte[] receivedMessage = new byte[65535];
            ottawaSocket = new DatagramSocket(Constants.MTL_OTW_PORT);
            torontoSocket = new DatagramSocket(Constants.MTL_TOR_PORT);
            do {
                // Ready to get message from toronto
                DatagramPacket receivePacketFromToronto = new DatagramPacket(receivedMessage, receivedMessage.length);
                torontoSocket.receive(receivePacketFromToronto);
                System.out.println("Received Message from Toronto " + new String(receivedMessage));
                receivedMessage = new byte[65535];

                // Ready to get message from ottawa
                DatagramPacket receivePacketFromOttawa = new DatagramPacket(receivedMessage, receivedMessage.length);
                ottawaSocket.receive(receivePacketFromOttawa);
                System.out.println("Received Message from Ottawa " + new String(receivedMessage));
                receivedMessage = new byte[65535];
            } while (!new String(receivedMessage).equals("bye"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

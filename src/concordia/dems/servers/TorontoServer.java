package concordia.dems.servers;

import concordia.dems.helpers.Constants;

import java.io.IOException;
import java.net.*;

public class TorontoServer {

    private DatagramSocket ottawaSocket = null, montrealSocket = null;
    private DatagramPacket datagramPacket = null;
    private InetAddress ipAddress = null;

    public static void main(String[] args) {
        new TorontoServer().initConnection();
    }

    private void initConnection() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            montrealSocket = new DatagramSocket(Constants.TOR_MTL_PORT);
            ottawaSocket = new DatagramSocket(Constants.TOR_OTW_PORT);
            InetAddress ipAddress = InetAddress.getLocalHost();
            byte[] receivedMessage = new byte[65535];
            do {
                // Ready to get message from montreal
                DatagramPacket receivePacketFromMontreal = new DatagramPacket(receivedMessage, receivedMessage.length);
                montrealSocket.receive(receivePacketFromMontreal);
                System.out.println("Received Message from Montreal " + new String(receivedMessage));
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

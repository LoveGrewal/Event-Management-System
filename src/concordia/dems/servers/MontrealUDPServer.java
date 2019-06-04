package concordia.dems.servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MontrealUDPServer implements Runnable {
    private static DatagramSocket aSocket;
    public void run() {
        try{
            aSocket = new DatagramSocket(8888);
            byte[] buffer;
            DatagramPacket request;
            while (true) {
                buffer = new byte[1000];
                System.out.println("Server waiting for request at 8888 ............");
                request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                //byte[] b = new String(request.getData()).getBytes();
                //new Thread(new Responder(b, request.getLength(), request.getAddress(), request.getPort(), aSocket)).start();
                new Thread(new MontrealResponder(request.getData(), request.getLength(), request.getAddress(), request.getPort(), aSocket)).start();
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }

    }

    public static void main(String[] args) {
        new MontrealUDPServer().run();
    }
}

class MontrealResponder implements Runnable {

    DatagramSocket aSocket;
    DatagramPacket reply;

    public MontrealResponder(byte[] data, int length, InetAddress add,  int port, DatagramSocket aSocket) {
        //this.request = request;
        this.aSocket = aSocket;
        reply = new DatagramPacket(data, length, add, port);
        System.out.println("Data :"+ new String(reply.getData()));
    }

    public void  run() {
        try{
            aSocket.send(reply);
        }catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
}

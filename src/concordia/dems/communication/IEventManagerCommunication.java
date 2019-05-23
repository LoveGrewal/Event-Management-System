package concordia.dems.communication;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface is a communication layer interface
 *
 * @author Mayank Jariwala
 * @version 1.0.0
 */
public interface IEventManagerCommunication extends Remote {

    void performOperation(String userRequest) throws RemoteException;
}

package concordia.dems.communication.impl;

import concordia.dems.communication.IEventManagerCommunication;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class EventManagerCommunication extends UnicastRemoteObject implements IEventManagerCommunication {

    protected EventManagerCommunication() throws RemoteException {
        super();
    }

    /**
     * @param userRequest: Either Manager or Customer
     */
    @Override
    public void performOperation(String userRequest) {

    }
}

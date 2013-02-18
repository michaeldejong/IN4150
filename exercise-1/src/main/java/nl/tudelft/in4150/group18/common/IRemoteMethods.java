package nl.tudelft.in4150.group18.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import nl.tudelft.in4150.group18.network.Address;

/**
 * Describes the methods a sender can call to the listener.
 * Listener should implement this method.
 */
public interface IRemoteMethods extends Remote {

	void message(Address from, String message) throws RemoteException;
	
}

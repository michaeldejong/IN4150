package nl.tudelft.in4150.group18.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import nl.tudelft.in4150.group18.network.Address;

public interface TotalOrdering_RMI extends Remote {

	void message(Address from, String message) throws RemoteException;
	
}

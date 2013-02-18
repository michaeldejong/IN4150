package nl.tudelft.in4150.group18.common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import nl.tudelft.in4150.group18.Main;
import nl.tudelft.in4150.group18.network.Address;
import nl.tudelft.in4150.group18.network.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class TotalOrdering extends UnicastRemoteObject implements TotalOrdering_RMI {

	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	public TotalOrdering(Node<TotalOrdering_RMI> node) throws RemoteException {
		super();
	}

	@Override
	public void message(Address from, String message) throws RemoteException {
		log.info("<<< Received message: \"" + message + "\" from: " + from);
	}

}

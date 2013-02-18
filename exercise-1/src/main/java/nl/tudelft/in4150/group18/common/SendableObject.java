package nl.tudelft.in4150.group18.common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import nl.tudelft.in4150.group18.Main;
import nl.tudelft.in4150.group18.network.Address;
import nl.tudelft.in4150.group18.network.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class SendableObject extends UnicastRemoteObject implements IRemoteMethods {

	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	/**
	 * Internal framework calls this constructor when a sender calls the receivers method published in the {@link IRemoteMethods}
	 * @param node this is us, the network representation of ourselves (use this to send and receive from the network); can be used to communicate from this call procedure to others on the network during the processing of this remote call.
	 * @throws RemoteException when something is wrong with the connection
	 */
	public SendableObject(Node<IRemoteMethods> node) throws RemoteException {
		super();
	}

	/**
	 * return type is the response to the caller
	 * this is send over the network
	 * timing is unknown
	 */
	@Override
	public void message(Address from, String message) throws RemoteException {
		log.info("<<< Received message: \"" + message + "\" from: " + from);
	}

}

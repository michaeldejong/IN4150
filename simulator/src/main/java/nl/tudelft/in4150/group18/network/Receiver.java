package nl.tudelft.in4150.group18.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import nl.tudelft.in4150.group18.common.IRemoteObject;
import nl.tudelft.in4150.group18.common.IRemoteObject.Message;

/**
 * This class is responsible for managing incoming connections.
 * 
 * @author michael
 *
 * @param <I>
 */
class Receiver<I extends Message> {

	private final Address address;
	private Registry registry = null;

	/**
	 * This constructor creates a new {@link Receiver} object.
	 * 
	 * @param host					The local node's {@link InetAddress}.
	 * @throws UnknownHostException	In case the {@link Receiver} is unable to create the RMI registry.
	 */
	public Receiver(InetAddress host) throws UnknownHostException {
		int port = 1100;
		while (registry == null) {
			try {
				registry = LocateRegistry.createRegistry(port);
			}
			catch (RemoteException e) {
				port++;
			}
		}
		
		this.address = new Address(host.getHostAddress(), port);
	}
	
	/**
	 * @return	The local node's {@link Address}.
	 */
	public Address getLocalAddress() {
		return address;
	}
	
	/**
	 * This method registers an {@link IRemoteObject} object with the RMI registry.
	 * 
	 * @param relay				The {@link IRemoteObject} to register with the RMI registry.
	 * @throws RemoteException	In case the {@link IRemoteObject} object could not be registered.
	 */
	void registerRelay(IRemoteObject<I> relay) throws RemoteException {
		registry.rebind("relay", relay);
	}

}

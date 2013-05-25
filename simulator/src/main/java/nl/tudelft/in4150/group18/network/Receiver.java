package nl.tudelft.in4150.group18.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import nl.tudelft.in4150.group18.common.IRemoteMessage;
import nl.tudelft.in4150.group18.common.IRemoteRequest;

/**
 * This class is responsible for managing incoming connections.
 * 
 * @author michael
 *
 * @param <I>
 */
class Receiver {

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
	 * This method registers an {@link IRemoteMessage} object with the RMI registry.
	 * 
	 * @param relay				The {@link IRemoteMessage} to register with the RMI registry.
	 * @throws RemoteException	In case the {@link IRemoteMessage} object could not be registered.
	 */
	void registerRelay(IRemoteMessage<?> relay) throws RemoteException {
		registry.rebind("relay", relay);
	}
	
	/**
	 * This method registers an {@link IRemoteRequest} object with the RMI registry.
	 * 
	 * @param relay				The {@link IRemoteRequest} to register with the RMI registry.
	 * @throws RemoteException	In case the {@link IRemoteRequest} object could not be registered.
	 */
	void registerRelay(IRemoteRequest<?, ?> relay) throws RemoteException {
		registry.rebind("relay", relay);
	}

}

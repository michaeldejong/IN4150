package nl.tudelft.in4150.group18.network;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import nl.tudelft.in4150.group18.common.IRemoteObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a representation of a remote node. This class allows you to lookup 
 * the {@link IRemoteObject} object from a remote node using Java RMI.
 * 
 * @author michael
 *
 * @param <T>
 */
class RemoteNode<T extends Remote> {

	private static final Logger log = LoggerFactory.getLogger(RemoteNode.class);
	
	private final Address address;
	
	private T remote = null;
	
	/**
	 * This constructs a new {@link RemoteNode}.
	 * 
	 * @param address		The {@link Address} of the remote node.
	 * @param localOnly		True, if the cluster is running on a single machine, false otherwise. 
	 */
	public RemoteNode(Address address, boolean localOnly) {
		this.address = address;
	}
	
	/**
	 * This method initializes the {@link Remote} object if required and returns it.
	 * 
	 * @return	The initialized or retrieved {@link Remote}.
	 * 
	 * @throws RemoteException	If something went wrong with the RMI service.
	 */
	@SuppressWarnings("unchecked")
	public T initialize() throws RemoteException {
		if (remote != null) {
			return remote;
		}
		
		try {
			log.trace("Doing lookup of proxy object for remote: {}", address);
			String rmiUrl = "rmi://" + address.getHostAddress() + ":" + address.getPort() + "/relay";
			remote = (T) Naming.lookup(rmiUrl);
			return remote;
		} 
		catch (MalformedURLException | NotBoundException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

}

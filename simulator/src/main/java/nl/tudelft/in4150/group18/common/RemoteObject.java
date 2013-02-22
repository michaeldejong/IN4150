package nl.tudelft.in4150.group18.common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

import nl.tudelft.in4150.group18.DistributedAlgorithm;
import nl.tudelft.in4150.group18.common.IRemoteObject.Message;
import nl.tudelft.in4150.group18.network.Address;
import nl.tudelft.in4150.group18.network.Node;

/**
 * This class is called by remote nodes, and relays the calls to the specified {@link DistributedAlgorithm}.
 * 
 * @author michael
 *
 * @param <M>
 */
@SuppressWarnings("serial")
public class RemoteObject<M extends Message> extends UnicastRemoteObject implements IRemoteObject<M> {

	private final DistributedAlgorithm<M> algorithm;
	private final Node<IRemoteObject<M>, M> node;

	/**
	 * This constructs a new {@link RemoteObject}.
	 * 
	 * @param algorithm			The {@link DistributedAlgorithm} to relay the messages to.
	 * @throws RemoteException	In case the {@link RemoteObject} could not be registered with RMI.
	 */
	public RemoteObject(Node<IRemoteObject<M>, M> node, DistributedAlgorithm<M> algorithm) throws RemoteException {
		super();
		this.node = node;
		this.algorithm = algorithm;
	}
	
	/**
	 * This method is called when receiving a method. It then relays 
	 * the message to the specified {@link DistributedAlgorithm}.
	 */
	@Override
	public void onMessage(M message, Address from) throws RemoteException {
		algorithm.onMessage(message, from);
	}

	@Override
	public Set<Address> discover() throws RemoteException {
		return node.listRemoteAddresses();
	}
	
}

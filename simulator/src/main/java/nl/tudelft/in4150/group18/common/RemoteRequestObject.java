package nl.tudelft.in4150.group18.common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

import nl.tudelft.in4150.group18.DistributedAlgorithm;
import nl.tudelft.in4150.group18.SynchronousDistributedAlgorithm;
import nl.tudelft.in4150.group18.common.IRemoteRequest.IRequest;
import nl.tudelft.in4150.group18.network.Address;
import nl.tudelft.in4150.group18.network.RequestingNode;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Every node in the network should have exactly one instance of this class. This object is then registered 
 * with a Java RMI Registry, and made available to other external Java Virtual Machines. This class
 * is capable of receiving messages from external nodes, which will be relayed to the specified {@link DistributedAlgorithm}.
 * 
 * @author michael
 *
 * @param <M>
 */
@SuppressWarnings("serial")
public class RemoteRequestObject<M extends IRequest, R> extends UnicastRemoteObject implements IRemoteRequest<M, R> {

	private final SynchronousDistributedAlgorithm<R> algorithm;
	private final RequestingNode<IRemoteRequest<M, R>, M, R> node;

	/**
	 * This constructs a new {@link RemoteRequestObject}.
	 * 
	 * @param algorithm			The {@link SynchronousDistributedAlgorithm} to relay the messages to.
	 * @throws RemoteException	In case the {@link RemoteRequestObject} could not be registered with RMI.
	 */
	public RemoteRequestObject(RequestingNode<IRemoteRequest<M, R>, M, R> node, SynchronousDistributedAlgorithm<R> algorithm) throws RemoteException {
		super();
		this.node = node;
		this.algorithm = algorithm;
	}
	
	/**
	 * This method is called when receiving a method. It then relays 
	 * the message to the specified {@link SynchronousDistributedAlgorithm}.
	 */
	@Override
	public R onRequest(M message, Address from) throws RemoteException {
		return algorithm.onRequest(message, from);
	}

	@Override
	public Set<Address> exchangeKnownAddresses(Set<Address> externalAddresses) throws RemoteException {
		Set<Address> knownRemotes = node.listAllAddresses();
		SetView<Address> newRemotes = Sets.difference(externalAddresses, knownRemotes);
		for (Address address : newRemotes) {
			node.addRemote(address).exchangeKnownAddresses(node.listAllAddresses());
		}
		
		return node.listAllAddresses();
	}
	
}

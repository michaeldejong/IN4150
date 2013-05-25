package nl.tudelft.in4150.group18.common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

import nl.tudelft.in4150.group18.DistributedAlgorithm;
import nl.tudelft.in4150.group18.common.IRemoteMessage.IMessage;
import nl.tudelft.in4150.group18.network.Address;
import nl.tudelft.in4150.group18.network.MessagingNode;

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
public class RemoteMessageObject<M extends IMessage> extends UnicastRemoteObject implements IRemoteMessage<M> {

	private final DistributedAlgorithm algorithm;
	private final MessagingNode<IRemoteMessage<M>, M> node;

	/**
	 * This constructs a new {@link RemoteMessageObject}.
	 * 
	 * @param algorithm			The {@link DistributedAlgorithm} to relay the messages to.
	 * @throws RemoteException	In case the {@link RemoteMessageObject} could not be registered with RMI.
	 */
	public RemoteMessageObject(MessagingNode<IRemoteMessage<M>, M> node, DistributedAlgorithm algorithm) throws RemoteException {
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
	public Set<Address> exchangeKnownAddresses(Set<Address> externalAddresses) throws RemoteException {
		Set<Address> knownRemotes = node.listAllAddresses();
		SetView<Address> newRemotes = Sets.difference(externalAddresses, knownRemotes);
		for (Address address : newRemotes) {
			node.addRemote(address).exchangeKnownAddresses(node.listAllAddresses());
		}
		
		return node.listAllAddresses();
	}
	
}

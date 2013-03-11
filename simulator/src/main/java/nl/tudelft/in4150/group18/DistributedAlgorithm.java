package nl.tudelft.in4150.group18;

import java.rmi.RemoteException;
import java.util.Collection;

import nl.tudelft.in4150.group18.common.IRemoteObject;
import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;
import nl.tudelft.in4150.group18.network.Node;

/**
 * This abstract class represents a distributed algorithm. To create and test
 * a distributed algorithm, simply create a new class which extends this class, 
 * and implements the missing methods.
 * 
 * @author michael
 *
 * @param <M>
 */
public abstract class DistributedAlgorithm {

	private Node<IRemoteObject<IMessage>, IMessage> node;

	/**
	 * This method will be called upon starting a {@link DistributedAlgorithm}. Note that only one
	 * {@link DistributedAlgorithm} class in a cluster will receive this method call. The others should
	 * be triggered by sending messages. This method merely serves as a trigger for the starting the
	 * algorithm in the cluster.
	 */
	public abstract void start();
	
	/**
	 * This method is called upon receiving a new {@link IMessage} from another remote.
	 * 
	 * @param message	The received {@link IMessage}.
	 * @param from		The {@link Address} of the remote which sent the {@link IMessage}.
	 */
	public abstract void onMessage(IMessage message, Address from);
	
	/**
	 * This method sets the {@link IRemoteObject} which the 
	 * {@link DistributedAlgorithm} should use for communication.
	 * 
	 * @param node	The {@link IRemoteObject} to use.
	 */
	void setNode(Node<IRemoteObject<IMessage>, IMessage> node) {
		this.node = node;
	}
	
	/**
	 * @return	A {@link Collection} of {@link Address}es of currently known remote processes.
	 */
	protected Collection<Address> getRemoteAddresses() {
		return node.listRemoteAddresses();
	}

	/**
	 * This method sends a {@link IMessage} to the specified {@link Address}.
	 * 
	 * @param content	The {@link IMessage} to send.
	 * @param address	The {@link Address} to send it to.
	 * 
	 * @throws RemoteException	In case we could not message specified remote.
	 */
	protected void send(IMessage content, Address address) throws RemoteException {
		node.send(content, address);
	}
	
	/**
	 * This method sends a {@link IMessage} to multiple remotes.
	 * 
	 * @param content	The {@link IMessage} to send.
	 * @param addresses	The {@link Address}es to send it to.
	 */
	protected void multicast(IMessage content, Collection<Address> addresses) {
		node.multicast(content, addresses);
	}

	/**
	 * This method sends a {@link IMessage} to all known remotes.
	 * 
	 * @param content	The {@link IMessage} to send.
	 */
	protected void broadcast(IMessage content) {
		node.broadcast(content);
	}

	/**
	 * @return	The {@link Address} of this local machine.
	 */
	protected Address getLocalAddress() {
		return node.getLocalAddress();
	}
	
}

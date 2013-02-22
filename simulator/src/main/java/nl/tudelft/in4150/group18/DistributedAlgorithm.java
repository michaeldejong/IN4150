package nl.tudelft.in4150.group18;

import java.rmi.RemoteException;
import java.util.Collection;

import nl.tudelft.in4150.group18.common.IRemoteObject;
import nl.tudelft.in4150.group18.common.IRemoteObject.Message;
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
public abstract class DistributedAlgorithm<M extends Message> {

	private Node<IRemoteObject<M>, M> node;

	/**
	 * This method will be called upon starting a {@link DistributedAlgorithm}. Note that only one
	 * {@link DistributedAlgorithm} class in a cluster will receive this method call. The others should
	 * be triggered by sending messages. This method merely serves as a trigger for the starting the
	 * algorithm in the cluster.
	 */
	public abstract void start();
	
	/**
	 * This method is called upon receiving a new {@link Message} from another remote.
	 * 
	 * @param message	The received {@link Message}.
	 * @param from		The {@link Address} of the remote which sent the {@link Message}.
	 */
	public abstract void onMessage(M message, Address from);
	
	/**
	 * This method sets the {@link IRemoteObject} which the 
	 * {@link DistributedAlgorithm} should use for communication.
	 * 
	 * @param node	The {@link IRemoteObject} to use.
	 */
	void setNode(Node<IRemoteObject<M>, M> node) {
		this.node = node;
	}
	
	/**
	 * @return	A {@link Collection} of {@link Address}es of currently known remote processes.
	 */
	protected Collection<Address> getRemoteProcesses() {
		return node.listRemoteAddresses();
	}

	/**
	 * This method sends a {@link Message} to the specified {@link Address}.
	 * 
	 * @param content	The {@link Message} to send.
	 * @param address	The {@link Address} to send it to.
	 * 
	 * @throws RemoteException	In case we could not message specified remote.
	 */
	protected void send(M content, Address address) throws RemoteException {
		node.send(content, address);
	}
	
	/**
	 * This method sends a {@link Message} to multiple remotes.
	 * 
	 * @param content	The {@link Message} to send.
	 * @param addresses	The {@link Address}es to send it to.
	 */
	protected void multicast(M content, Address... addresses) {
		node.multicast(content, addresses);
	}
	
	/**
	 * This method sends a {@link Message} to multiple remotes.
	 * 
	 * @param content	The {@link Message} to send.
	 * @param addresses	The {@link Address}es to send it to.
	 */
	protected void multicast(M content, Collection<Address> addresses) {
		node.multicast(content, addresses);
	}

	/**
	 * This method sends a {@link Message} to all known remotes.
	 * 
	 * @param content	The {@link Message} to send.
	 * @param loopback	True if you want the sender to receive its own {@link Message} or 
	 * 					false if you don't want the sender to receive its own {@link Message}.
	 */
	protected void broadcast(M content, boolean loopback) {
		node.broadcast(content, loopback);
	}

	/**
	 * @return	The {@link Address} of this local machine.
	 */
	protected Address getLocalAddress() {
		return node.getLocalAddress();
	}
	
}

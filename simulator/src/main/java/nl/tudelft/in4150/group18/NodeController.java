package nl.tudelft.in4150.group18;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Collection;

import nl.tudelft.in4150.group18.common.IRemoteObject;
import nl.tudelft.in4150.group18.common.IRemoteObject.Message;
import nl.tudelft.in4150.group18.common.RemoteObject;
import nl.tudelft.in4150.group18.network.Address;
import nl.tudelft.in4150.group18.network.Node;

/**
 * This is a wrapper class for the {@link Node} class. It adds functionality to manage
 * a specified {@link DistributedAlgorithm} object, and various helper methods.
 * 
 * @author michael
 * @param <M>
 */
public class NodeController<M extends Message> {
	
	private final Node<IRemoteObject<M>, M> node;
	private final DistributedAlgorithm<M> algorithm;

	/**
	 * Constructs a new {@link NodeController} object.
	 * 
	 * @param address		The {@link InetAddress} of the local JVM.
	 * @param localOnly		True if this cluster runs on a single machine or false if it runs across a network.
	 * @param algorithm		The {@link DistributedAlgorithm} to run across this cluster.
	 * 
	 * @throws IOException	In case there were problems setting up Java RMI.
	 */
	public NodeController(InetAddress address, boolean localOnly, DistributedAlgorithm<M> algorithm) throws IOException {
		this.algorithm = algorithm;
		this.node = new Node<>(address, localOnly);
		this.node.registerRelay(new RemoteObject<>(node, algorithm));
		algorithm.setNode(node);
	}
	
	/**
	 * Calling this method will tell the {@link DistributedAlgorithm} specified in the constructor, to start.
	 */
	public void start() {
		algorithm.start();
	}
	
	/**
	 * When you call this method, all future messages will be not be directly sent.
	 * Once you call {@link NodeController#releaseMessages()} these messages will be sent.
	 */
	public void holdMessages() {
		node.holdMessages();
	}
	
	/**
	 * When you call this method, all pending outgoing messages are sent, and future
	 * messages will immediately be sent and not be stored in a queue.
	 */
	public void releaseMessages() {
		node.releaseMessages();
	}

	/**
	 * When you call this method, the {@link NodeController} will attempt to locate other remotes in the cluster.
	 */
	public void autoDetectRemotes() {
		node.autoDetectRemotes();
	}

	/**
	 * @return	A {@link Collection} of {@link Address}es of currently known remotes.
	 */
	public Collection<Address> listRemoteAddresses() {
		return node.listRemoteAddresses();
	}

	/**
	 * @return	The {@link Address} of the local node.
	 */
	public Address getLocalAddress() {
		return node.getLocalAddress();
	}

	/**
	 * This method allows you to add a remote manually.
	 * 
	 * @param address			The {@link Address} of the remote you want to add.
	 * @throws RemoteException	If the {@link NodeController} could not connect to a remote on the specified {@link Address}.
	 */
	public void addRemote(Address address) throws RemoteException {
		node.addRemote(address);
	}

}
package nl.tudelft.in4150.group18;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Collection;

import nl.tudelft.in4150.group18.common.IRemoteObject;
import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
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
public class NodeController {
	
	private final Node<IRemoteObject<IMessage>, IMessage> node;
	private final DistributedAlgorithm algorithm;

	/**
	 * Constructs a new {@link NodeController} object.
	 * 
	 * @param address		The {@link InetAddress} of the local JVM.
	 * @param localOnly		True if this cluster runs on a single machine or false if it runs across a network.
	 * @param algorithm		The {@link DistributedAlgorithm} to run across this cluster.
	 * 
	 * @throws IOException	In case there were problems setting up Java RMI.
	 */
	public NodeController(InetAddress address, boolean localOnly, DistributedAlgorithm algorithm) throws IOException {
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
	 * Calling this message will cause this {@link NodeController} to queue 
	 * the messages to send, and not send them to their destinations.
	 * 
	 * To release these {@link IMessage}s again, see {@link NodeController#releaseMessages()}.
	 */
	public void holdMessagesToSend() {
		node.holdMessages();
	}
	
	/**
	 * Calling this message will cause this {@link NodeController} to flush 
	 * its buffer of messages to their respective destinations. Additionally
	 * new {@link IMessage} will be allowed to be sent immediately.
	 * 
	 * Also see {@link NodeController#holdMessagesToSend()}.
	 */
	public void releaseMessages() {
		node.releaseMessages();
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

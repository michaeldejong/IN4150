package nl.tudelft.in4150.group18.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import nl.tudelft.in4150.group18.common.IRemoteObject;
import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;

/**
 * This class contains an internal {@link Receiver} to process messages from remotes, and a collection 
 * of {@link RemoteNode}s which can be used to send messages to them.
 * 
 * @author michael
 *
 * @param <I>
 * @param <M>
 */
public class Node<I extends IRemoteObject<M>, M extends IMessage> {
	
	private static final Range<Integer> PORT_RANGE = Range.closed(1100, 1200);
	private static final Logger log = LoggerFactory.getLogger(Node.class);
	
	private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
	
	private final Receiver<M> receiver;
	private final Map<Address, RemoteNode<I>> remotes;
	private final boolean localOnly;
	private final InetAddress address;
	
	private final Object notifier = new Object();
	private volatile boolean holdMessages = false;
	
	/**
	 * This constructs a new {@link Node} object.
	 * 
	 * @param address	The {@link Address} of the local node.
	 * @param localOnly	True if the cluster will run on a single machine, or false otherwise.
	 * 
	 * @throws UnknownHostException	In case of network issues.
	 */
	public Node(InetAddress address, boolean localOnly) throws UnknownHostException {
		if (!localOnly && System.getSecurityManager() == null) {
			log.info("Setting RMI SecurityManager...");
			System.setSecurityManager(new RMISecurityManager());
		}
		
		this.address = address;
		this.receiver = new Receiver<>(address);
		this.remotes = Maps.newConcurrentMap();
		this.localOnly = localOnly;
	}
	
	/**
	 * This method registers the {@link IRemoteObject} with the {@link Node}. In essence this
	 * method registers the {@link IRemoteObject} to the RMI {@link Registry} for use
	 * by remotes.
	 * 
	 * @param relay				The {@link IRemoteObject} to register with the RMI registry.
	 * @throws RemoteException	If the {@link IRemoteObject} could not be registered.
	 */
	public void registerRelay(IRemoteObject<M> relay) throws RemoteException {
		receiver.registerRelay(relay);
	}
	
	/**
	 * @return	A {@link Collection} of {@link Address}es of currently known remotes.
	 */
	public Set<Address> listRemoteAddresses() {
		return Collections.unmodifiableSet(remotes.keySet());
	}

	/**
	 * Calling this message will cause this {@link Node} to queue 
	 * the messages to send, and not send them to their destinations.
	 * 
	 * To release these {@link IMessage}s again, see {@link Node#releaseMessages()}.
	 */
	public void holdMessages() {
		synchronized (notifier) {
			holdMessages = true;
			notifier.notifyAll();
		}
	}
	
	/**
	 * Calling this message will cause this {@link Node} to flush 
	 * its buffer of messages to their respective destinations. Additionally
	 * new {@link IMessage} will be allowed to be sent immediately.
	 * 
	 * Also see {@link Node#holdMessages()}.
	 */
	public void releaseMessages() {
		synchronized (notifier) {
			holdMessages = false;
			notifier.notifyAll();
		}
	}
	
	/**
	 * This method sends a {@link IMessage} to the specified {@link Address}.
	 * 
	 * @param message	The {@link IMessage} to send.
	 * @param to		The {@link Address} to send the {@link IMessage} to.
	 */
	public void send(final M message, final Address to) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				waitUntilAllowedToSend();
				
				try {
					log.debug(getLocalAddress() + " - Sending {}: {} to: {}", message.getClass().getSimpleName(), message, to);
					getRemote(to).onMessage(message, getLocalAddress());
				}
				catch (RemoteException e) {
					log.warn(getLocalAddress() + " - Remote doesn't seem to be online anymore.", e);
					remotes.remove(to);
				}
			}

			private void waitUntilAllowedToSend() {
				while (holdMessages) {
					try {
						synchronized (notifier) {
							notifier.wait();
						}
					} catch (InterruptedException e) {
						log.warn(getLocalAddress() + " - Was interrupted while waiting for messages to be released.", e);
					}
				}
			}
		});
	}
	
	/**
	 * This method sends a {@link IMessage} to every {@link Address} specified.
	 * 
	 * @param message	The {@link IMessage} to send.
	 * @param to		The {@link Address}es to send the {@link IMessage} to.
	 */
	public void multicast(M message, Collection<Address> to) {
		for (Address address : to) {
			send(message, address);
		}
	}
	
	/**
	 * This method sends a {@link IMessage} to every {@link Address} specified.
	 * 
	 * @param message	The {@link IMessage} to send.
	 */
	public void broadcast(M message) {
		multicast(message, listRemoteAddresses());
	}

	/**
	 * This method tries to discover remotes automatically, by trying to connecting to
	 * remotes on ports in a specific range.
	 */
	public void autoDetectRemotes() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			if (localOnly) {
				scanPorts(localHost.getHostAddress());
			}
			else {
				byte[] localAddress = address.getAddress();
				for (int i = 0; i < 256; i++) {
					if (localAddress[localAddress.length - 1] != (byte) i) {
						byte[] address = new byte[localAddress.length];
						for (int j = 0; j < localAddress.length - 1; j++) {
							address[j] = localAddress[j];
						}
						address[localAddress.length - 1] = (byte) i;
						scanPorts(InetAddress.getByAddress(address).getHostAddress());
					}
				}
			}
		}
		catch (UnknownHostException e) {
			log.warn(e.getMessage(), e);
		}
	}

	private void scanPorts(String hostAddress) {
		for (int port = PORT_RANGE.lowerEndpoint(); port <= PORT_RANGE.upperEndpoint(); port++) {
			try {
				scanRemote(new Address(hostAddress, port));
			} catch (RemoteException e) {
				continue;
			}
		}
	}
	
	/**
	 * @return	The {@link Address} of the local {@link Node}.
	 */
	public Address getLocalAddress() {
		return receiver.getLocalAddress();
	}

	/**
	 * This method connects to a specified {@link Address} and adds it to the known remotes,
	 * if it is able to establish a connection.
	 * 
	 * @param address			The {@link Address} to add.
	 * @return					The proxy for the remote itself.
	 * @throws RemoteException	If the remote could not be added.
	 */
	public I addRemote(Address address) throws RemoteException {
		return getRemote(address);
	}
	
	private void scanRemote(Address address) throws RemoteException {
		Set<Address> externalRemotes = getRemote(address).discover();
		for (Address remote : externalRemotes) {
			getRemote(remote);
		}
	}
	
	private I getRemote(Address address) throws RemoteException {
		if (remotes.containsKey(address)) {
			log.trace(getLocalAddress() + " - Retrieving proxy object for remote: {}", address);
			RemoteNode<I>client = remotes.get(address);
			return client.initialize();
		}
		else {
			log.trace(getLocalAddress() + " - Creating new proxy object for remote: {}", address);
			RemoteNode<I> client = new RemoteNode<>(address, localOnly);
			I relay = client.initialize();
			remotes.put(address, client);
			
			return relay;
		}
	}
	
}

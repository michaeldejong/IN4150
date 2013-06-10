package nl.tudelft.in4150.group18.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import nl.tudelft.in4150.group18.common.IRemoteMessage;
import nl.tudelft.in4150.group18.common.IRemoteMessage.IMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

/**
 * This class contains an internal {@link Receiver} to process messages from remotes, and a collection 
 * of {@link RemoteNode}s which can be used to send messages to them.
 * 
 * @author michael
 *
 * @param <I>
 * @param <M>
 */
public class MessagingNode<I extends IRemoteMessage<M>, M extends IMessage> {
	
	private static final Range<Integer> PORT_RANGE = Range.closed(1100, 1200);
	private static final Logger log = LoggerFactory.getLogger(MessagingNode.class);
	
	private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
	
	private final Receiver receiver;
	private final Map<Address, RemoteNode<I>> remotes;
	private final boolean localOnly;
	private final InetAddress address;
	
	private final Object notifier = new Object();
	private volatile boolean holdMessages = false;
	
	/**
	 * This constructs a new {@link MessagingNode} object.
	 * 
	 * @param address	The {@link Address} of the local node.
	 * @param localOnly	True if the cluster will run on a single machine, or false otherwise.
	 * 
	 * @throws UnknownHostException	In case of network issues.
	 */
	public MessagingNode(InetAddress address, boolean localOnly) throws UnknownHostException {
		if (!localOnly && System.getSecurityManager() == null) {
			log.info("Setting RMI SecurityManager...");
			System.setSecurityManager(new RMISecurityManager());
		}
		
		this.address = address;
		this.receiver = new Receiver(address);
		this.remotes = Maps.newConcurrentMap();
		this.localOnly = localOnly;
	}
	
	/**
	 * This method registers the {@link IRemoteMessage} with the {@link MessagingNode}. In essence this
	 * method registers the {@link IRemoteMessage} to the RMI {@link Registry} for use
	 * by remotes.
	 * 
	 * @param relay				The {@link IRemoteMessage} to register with the RMI registry.
	 * @throws RemoteException	If the {@link IRemoteMessage} could not be registered.
	 */
	public void registerRelay(IRemoteMessage<M> relay) throws RemoteException {
		receiver.registerRelay(relay);
	}
	
	/**
	 * @return	A {@link Collection} of {@link Address}es of currently known remotes.
	 */
	public Set<Address> listRemoteAddresses() {
		return Collections.unmodifiableSet(remotes.keySet());
	}
	
	/**
	 * @return	A {@link Collection} of {@link Address}es of currently known remotes.
	 */
	public Set<Address> listAllAddresses() {
		HashSet<Address> addresses = Sets.newHashSet(remotes.keySet());
		addresses.add(getLocalAddress());
		return Collections.unmodifiableSet(addresses);
	}

	/**
	 * Calling this message will cause this {@link MessagingNode} to queue 
	 * the messages to send, and not send them to their destinations.
	 * 
	 * To release these {@link IMessage}s again, see {@link MessagingNode#releaseMessages()}.
	 */
	public void holdMessages() {
		synchronized (notifier) {
			holdMessages = true;
			notifier.notifyAll();
		}
	}
	
	/**
	 * Calling this message will cause this {@link MessagingNode} to flush 
	 * its buffer of messages to their respective destinations. Additionally
	 * new {@link IMessage} will be allowed to be sent immediately.
	 * 
	 * Also see {@link MessagingNode#holdMessages()}.
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
	public Future<?> send(final M message, final Address to) {
		return executor.submit(new Runnable() {
			@Override
			public void run() {
				sendSynchronous(message, to);
			}
		});
	}
	
	public void sendSynchronous(M message, Address to) {
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
	 * @param to		The {@link Address}es to send the {@link IMessage} to.
	 */
	public void multicastSynchronous(M message, Collection<Address> to) {
		for (Address address : to) {
			sendSynchronous(message, address);
		}
	}
	
	/**
	 * This method sends a {@link IMessage} to every {@link Address} specified.
	 * 
	 * @param message	The {@link IMessage} to send.
	 * @param to		The {@link Address}es to send the {@link IMessage} to.
	 */
	public void multicastWait(M message, Collection<Address> to) {
		List<Future<?>> futures = Lists.newLinkedList();
		for (Address address : to) {
			futures.add(send(message, address));
		}
		
		while (!futures.isEmpty()) {
			Future<?> future = futures.get(0);
			if (future.isDone() || future.isCancelled()) {
				futures.remove(0);
			}
		}
	}
	
	/**
	 * This method sends a {@link IMessage} to every currently known {@link Address}.
	 * 
	 * @param message	The {@link IMessage} to send.
	 */
	public void broadcast(M message) {
		multicast(message, listRemoteAddresses());
	}
	
	/**
	 * This method sends a {@link IMessage} to every currently known {@link Address}.
	 * 
	 * @param message	The {@link IMessage} to send.
	 */
	public void broadcastSynchronous(M message) {
		multicastSynchronous(message, listRemoteAddresses());
	}
	
	/**
	 * This method sends a {@link IMessage} to every currently known {@link Address}.
	 * 
	 * @param message	The {@link IMessage} to send.
	 */
	public void broadcastWait(M message) {
		multicastWait(message, listRemoteAddresses());
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
	 * @return	The {@link Address} of the local {@link MessagingNode}.
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
		Set<Address> externalRemotes = getRemote(address).exchangeKnownAddresses(listAllAddresses());
		for (Address remote : externalRemotes) {
			getRemote(remote).exchangeKnownAddresses(listAllAddresses());
		}
	}
	
	private I getRemote(Address address) throws RemoteException {
		if (address.equals(getLocalAddress())) {
			throw new RemoteException("Cannot connect to self.");
		}
		
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

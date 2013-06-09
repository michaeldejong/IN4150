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
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nl.tudelft.in4150.group18.common.IRemoteRequest;
import nl.tudelft.in4150.group18.common.IRemoteRequest.IRequest;
import nl.tudelft.in4150.group18.ui.GraphDialog;

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
public class RequestingNode<I extends IRemoteRequest<M, R>, M extends IRequest, R> {
	
	private static final Range<Integer> PORT_RANGE = Range.closed(1100, 1111);
	private static final Logger log = LoggerFactory.getLogger(RequestingNode.class);
	
	private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);

	private final Receiver receiver;
	private final Map<Address, RemoteNode<I>> remotes;
	private final boolean localOnly;
	private final InetAddress address;
	
	private final Object notifier = new Object();
	private volatile boolean holdMessages = false;
	
	/**
	 * This constructs a new {@link RequestingNode} object.
	 * 
	 * @param address	The {@link Address} of the local node.
	 * @param localOnly	True if the cluster will run on a single machine, or false otherwise.
	 * 
	 * @throws UnknownHostException	In case of network issues.
	 */
	public RequestingNode(InetAddress address, boolean localOnly) throws UnknownHostException {
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
	 * This method registers the {@link IRemoteRequest} with the {@link RequestingNode}. In essence this
	 * method registers the {@link IRemoteRequest} to the RMI {@link Registry} for use
	 * by remotes.
	 * 
	 * @param relay				The {@link IRemoteRequest} to register with the RMI registry.
	 * @throws RemoteException	If the {@link IRemoteRequest} could not be registered.
	 */
	public void registerRelay(IRemoteRequest<?, ?> relay) throws RemoteException {
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
	 * Calling this message will cause this {@link RequestingNode} to queue 
	 * the messages to send, and not send them to their destinations.
	 * 
	 * To release these {@link IRequest}s again, see {@link RequestingNode#releaseMessages()}.
	 */
	public void holdMessages() {
		synchronized (notifier) {
			holdMessages = true;
			notifier.notifyAll();
		}
	}
	
	/**
	 * Calling this message will cause this {@link RequestingNode} to flush 
	 * its buffer of messages to their respective destinations. Additionally
	 * new {@link IRequest} will be allowed to be sent immediately.
	 * 
	 * Also see {@link RequestingNode#holdMessages()}.
	 */
	public void releaseMessages() {
		synchronized (notifier) {
			holdMessages = false;
			notifier.notifyAll();
		}
	}
	
	/**
	 * This method sends a {@link IRequest} to the specified {@link Address}.
	 * 
	 * @param message	The {@link IRequest} to send.
	 * @param to		The {@link Address} to send the {@link IRequest} to.
	 */
	public Future<R> send(final M message, final Address to) {
		return executor.submit(new Callable<R>() {
			@Override
			public R call() {
				return sendNow(message, to);
			}
		});
	}
	
	public R sendNow(M message, Address to) {
		waitUntilAllowedToSend();
		
		try {
			log.info(getLocalAddress() + " - Sending {}: {} to: {}", message.getClass().getSimpleName(), message, to);
			
			GraphDialog.getInstance().addEdge("" + getLocalAddress().getPort(), "" + to.getPort(), message.toString());
			
			return getRemote(to).onRequest(message, getLocalAddress());
		}
		catch (RemoteException e) {
			log.warn(getLocalAddress() + " - Remote doesn't seem to be online anymore.", e);
			remotes.remove(to);
		}
		
		return null;
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
	 * This method sends a {@link IRequest} to every {@link Address} specified.
	 * 
	 * @param message	The {@link IRequest} to send.
	 * @param to		The {@link Address}es to send the {@link IRequest} to.
	 * @param timeout 
	 */
	public Map<Address, R> multicast(M message, Collection<Address> to, int timeout, R defaultValue) {
		Map<Address, Future<R>> futures = Maps.newHashMap();
		for (Address address : to) {
			futures.put(address, send(message, address));
		}
		
		List<Address> notResponded = Lists.newArrayList(to);
		Map<Address, R> responses = Maps.newHashMap();
		while (!notResponded.isEmpty()) {
			Address address = notResponded.get(0);
			Future<R> future = futures.get(address);
			try {
				if (timeout > 0) {
					responses.put(address, future.get(timeout, TimeUnit.MILLISECONDS));
				}
				else {
					responses.put(address, future.get());
				}
			} 
			catch (Exception e) {
				responses.put(address, defaultValue);
			}
			notResponded.remove(address);
		}
		
		return responses;
	}
	
	/**
	 * This method sends a {@link IRequest} to every currently known {@link Address}.
	 * 
	 * @param message	The {@link IRequest} to send.
	 * @return 
	 */
	public Map<Address, R> broadcast(M message, int timeout, R defaultValue) {
		return multicast(message, listRemoteAddresses(), timeout, defaultValue);
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
	 * @return	The {@link Address} of the local {@link RequestingNode}.
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

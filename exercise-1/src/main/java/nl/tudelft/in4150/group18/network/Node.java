package nl.tudelft.in4150.group18.network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;

public class Node<I extends Remote> {

	private final String host;
	private final int port;
	private final boolean local;
	
	private final Listener<I> listener;
	private final Map<String, Sender<I>> senders;
	
	public Node(String host, int port, boolean local) {
		this.host = host;
		this.port = port;
		this.local = local;
		this.listener = new Listener<>(port, local);
		this.senders = Maps.newHashMap();
	}
	
	public void start(I relay) throws RemoteException {
		listener.start(relay);
	}
	
	public Collection<Sender<I>> listClients() {
		return Collections.unmodifiableCollection(senders.values());
	}

	public I getSender(Address client) throws RemoteException {
		return getSender(client.getHost(), client.getPort());
	}

	public I getSender(String host, int port) throws RemoteException {
		String address = host + ":" + port;
		
		if (senders.containsKey(address)) {
			Sender<I>client = senders.get(address);
			return client.start();
		}
		else {
			Sender<I> client = new Sender<>(host, port, local);
			I relay = client.start();
			senders.put(address, client);
			return relay;
		}
	}

	public Address getLocalAddress() {
		return new Address(host, port);
	}
	
}

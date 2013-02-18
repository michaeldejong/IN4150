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
	
	private final Server<I> server;
	private final Map<String, Client<I>> clients;
	
	public Node(String host, int port, boolean local) {
		this.host = host;
		this.port = port;
		this.local = local;
		this.server = new Server<>(port, local);
		this.clients = Maps.newHashMap();
	}
	
	public void start(I relay) throws RemoteException {
		server.start(relay);
	}
	
	public Collection<Client<I>> listClients() {
		return Collections.unmodifiableCollection(clients.values());
	}

	public I getClient(Address client) throws RemoteException {
		return getClient(client.getHost(), client.getPort());
	}

	public I getClient(String host, int port) throws RemoteException {
		String address = host + ":" + port;
		
		if (clients.containsKey(address)) {
			Client<I>client = clients.get(address);
			return client.start();
		}
		else {
			Client<I> client = new Client<>(host, port, local);
			I relay = client.start();
			clients.put(address, client);
			return relay;
		}
	}

	public Address getLocalAddress() {
		return new Address(host, port);
	}
	
}

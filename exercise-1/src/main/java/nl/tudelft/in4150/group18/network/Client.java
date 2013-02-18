package nl.tudelft.in4150.group18.network;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Client<T extends Remote> {

	private static final Logger log = LoggerFactory.getLogger(Client.class);
	
	private final String host;
	private final int port;
	
	private T remote = null;
	
	public Client(String host, int port, boolean local) {
		this.host = host;
		this.port = port;
		if (!local && System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
	}
	
	@SuppressWarnings("unchecked")
	public T start() throws RemoteException {
		if (remote != null) {
			return remote;
		}
		
		try {
			remote = (T) Naming.lookup("rmi://" + host + ":" + port + "/relay");
			return remote;
		} 
		catch (MalformedURLException | NotBoundException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

}

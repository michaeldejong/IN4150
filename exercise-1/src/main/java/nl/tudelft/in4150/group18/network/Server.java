package nl.tudelft.in4150.group18.network;

import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class Server<T extends Remote> {

	private final int port;

	public Server(int port, boolean local) {
		this.port = port;
		
		if (!local && System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
	}
	
	void start(T relay) throws RemoteException {
		Registry registry = LocateRegistry.createRegistry(port);
		registry.rebind("relay", relay);
	}

}

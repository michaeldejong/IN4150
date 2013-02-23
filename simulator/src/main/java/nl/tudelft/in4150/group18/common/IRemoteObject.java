package nl.tudelft.in4150.group18.common;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Set;

import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

/**
 * Describes the methods a node can call on remote nodes.
 */
public interface IRemoteObject<M extends IMessage> extends Remote {

	/**
	 * This method will be called upon receiving a {@link IMessage}.
	 * 
	 * @param message	The received {@link IMessage}.
	 * @param from		The {@link Address} of the node which sent it.
	 * 
	 * @throws RemoteException	In case a RMI exception occurred.
	 */
	void onMessage(M message, Address from) throws RemoteException;
	
	/**
	 * @return	a {@link Collection} of {@link Address}es of remotes of which 
	 * 			the external remote knows about.
	 * 
	 * @throws RemoteException	In case a RMI exception occurred.
	 */
	Set<Address> discover() throws RemoteException;

	/**
	 * A simple empty class which can be extended with additional fields to convey additional information.
	 */
	public interface IMessage extends Serializable { }
	
}

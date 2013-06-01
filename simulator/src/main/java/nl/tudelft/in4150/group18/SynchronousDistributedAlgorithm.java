package nl.tudelft.in4150.group18;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import nl.tudelft.in4150.group18.common.IRemoteRequest;
import nl.tudelft.in4150.group18.common.IRemoteRequest.IRequest;
import nl.tudelft.in4150.group18.network.Address;
import nl.tudelft.in4150.group18.network.RequestingNode;

/**
 * This abstract class represents a distributed algorithm. To create and test
 * a distributed algorithm, simply create a new class which extends this class, 
 * and implements the missing methods.
 * 
 * @author michael
 *
 * @param <M>
 */
public abstract class SynchronousDistributedAlgorithm<R> {

	private RequestingNode<IRemoteRequest<IRequest, R>, IRequest, R> node;

	/**
	 * This method will be called upon starting a {@link SynchronousDistributedAlgorithm}. Note that only one
	 * {@link SynchronousDistributedAlgorithm} class in a cluster will receive this method call. The others should
	 * be triggered by sending messages. This method merely serves as a trigger for the starting the
	 * algorithm in the cluster.
	 */
	public abstract void start();
	
	/**
	 * This method is called upon receiving a new {@link IRequest} from another remote.
	 * 
	 * @param message	The received {@link IRequest}.
	 * @param from		The {@link Address} of the remote which sent the {@link IRequest}.
	 */
	public abstract R onRequest(IRequest message, Address from);
	
	/**
	 * This method sets the {@link IRemoteRequest} which the 
	 * {@link SynchronousDistributedAlgorithm} should use for communication.
	 * 
	 * @param node	The {@link IRemoteRequest} to use.
	 */
	void setNode(RequestingNode<IRemoteRequest<IRequest, R>, IRequest, R> node) {
		this.node = node;
	}
	
	/**
	 * @return	A {@link Collection} of {@link Address}es of currently known remote processes.
	 */
	protected Collection<Address> getRemoteAddresses() {
		return node.listRemoteAddresses();
	}

	/**
	 * This method sends a {@link IRequest} to the specified {@link Address}.
	 * 
	 * @param content	The {@link IRequest} to send.
	 * @param address	The {@link Address} to send it to.
	 * 
	 * @throws RemoteException	In case we could not message specified remote.
	 */
	protected R send(IRequest content, Address address) throws RemoteException {
		return node.sendNow(content, address);
	}
	
	protected Future<R> sendAwait(IRequest content, Address address) throws RemoteException {
		return node.send(content, address);
	}
	
	/**
	 * This method sends a {@link IRequest} to multiple remotes.
	 * 
	 * @param content	The {@link IRequest} to send.
	 * @param addresses	The {@link Address}es to send it to.
	 * @param timeout 
	 * @param defaultValue 
	 */
	protected Map<Address, R> multicast(IRequest content, Collection<Address> addresses, int timeout, R defaultValue) {
		return node.multicast(content, addresses, timeout, defaultValue);
	}

	/**
	 * This method sends a {@link IRequest} to all known remotes.
	 * 
	 * @param content	The {@link IRequest} to send.
	 */
	protected Map<Address, R> broadcast(IRequest content, int timeout, R defaultValue) {
		return node.broadcast(content, timeout, defaultValue);
	}

	/**
	 * @return	The {@link Address} of this local machine.
	 */
	protected Address getLocalAddress() {
		return node.getLocalAddress();
	}
	
}

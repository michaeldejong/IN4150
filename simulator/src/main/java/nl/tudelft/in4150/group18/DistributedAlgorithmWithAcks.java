package nl.tudelft.in4150.group18;

import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a small extension of the {@link DistributedAlgorithm} class. 
 * This class specifically add support for sending and receiving acknowledgements.
 */
public abstract class DistributedAlgorithmWithAcks<M extends IMessage, A extends IAck> extends DistributedAlgorithm<IMessage> {

	private static final Logger log = LoggerFactory.getLogger(DistributedAlgorithmWithAcks.class);

	@Override
	@SuppressWarnings("unchecked")
	public final void onMessage(IMessage message, Address from) {
		if (message instanceof IAck) {
			log.debug("{}: Received ACK {} from {}", getLocalAddress(), message, from);
			onAcknowledgement((A) message, from);
			return;
		}
		log.debug("{}: Received MESSAGE {} from {}", getLocalAddress(), message, from);
		onMessageReceived((M) message, from);
	}
	
	protected abstract void onAcknowledgement(A message, Address from);
	
	protected abstract void onMessageReceived(M message, Address from);
	
}

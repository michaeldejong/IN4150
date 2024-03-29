package nl.tudelft.in4150.group18;

import nl.tudelft.in4150.group18.common.IRemoteMessage.IMessage;
import nl.tudelft.in4150.group18.network.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a small extension of the {@link DistributedAlgorithm} class. 
 * This class specifically add support for sending and receiving acknowledgements.
 */
public abstract class DistributedAlgorithmWithAcks<M extends IMessage, A extends IAck> extends DistributedAlgorithm {

	private static final Logger log = LoggerFactory.getLogger(DistributedAlgorithmWithAcks.class);

	@Override
	@SuppressWarnings("unchecked")
	public final void onMessage(IMessage message, Address from) {
		if (message instanceof IAck) {
			log.trace(getLocalAddress() + " - Received ACK {} from {}", message, from);
			onAcknowledgement((A) message, from);
			return;
		}
		log.trace(getLocalAddress() + " - Received MESSAGE {} from {}", message, from);
		onMessageReceived((M) message, from);
	}
	
	protected abstract void onAcknowledgement(A message, Address from);
	
	protected abstract void onMessageReceived(M message, Address from);
	
}

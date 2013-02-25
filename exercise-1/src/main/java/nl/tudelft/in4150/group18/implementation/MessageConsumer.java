package nl.tudelft.in4150.group18.implementation;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Receives messages after delivery
 */
public class MessageConsumer {

	private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

	private final List<Message> receivedMessages = Lists.newArrayList();
	private final AtomicLong lastMessageId = new AtomicLong(-1);

	private final Object lock = new Object();

	/**
	 * Checks validity of messages that are delivered
	 * @param message delivered
	 * @throws message delivered out of order exception (should never occur -> runtime exception)
	 */
	public void deliver(Message message) {
		synchronized (lock) {
			MessageIdentifier messageId = message.getId();
			if (messageId.getTimestamp() < lastMessageId.get()) {
				log.error("Received unexpected Message: " + message 
						+ " <-> Last received messageId: " + lastMessageId);
				
				throw new MessageDeliveredOutOfOrderException("Received message: " + message.getId()
						+ ", but expected: " + lastMessageId);
			}

			lastMessageId.set(messageId.getTimestamp());
			receivedMessages.add(message);
		}
	}

	public List<Message> getReceivedMessages() {
		synchronized (lock) {
			return Collections.unmodifiableList(receivedMessages);
		}
	}

	/**
	 * Convenience method
	 * @return Amount of messages delivered on this node
	 */
	public int numberOfMessagesDelivered() {
		return getReceivedMessages().size();
	}

}

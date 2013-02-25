package nl.tudelft.in4150.group18.implementation;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;


public class MessageConsumer {

	private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);
	
	private final List<Message> receivedMessages = Lists.newArrayList();
	private final AtomicLong lastReceivedTimestamp = new AtomicLong(-1);
	private final AtomicBoolean receivedAllMessagesInOrder = new AtomicBoolean(true);
	
	private final Object lock = new Object();
	
	public void deliver(Message message) {
		log.info("Delivered message: {}", message);
		synchronized (lock) {
			MessageIdentifier timestamp = message.getTimestamp();
			if (timestamp.getTimestamp() < lastReceivedTimestamp.get()) {
				log.error("Received unexpected Message: " + message + " <-> Last received timestamp: " + lastReceivedTimestamp);
				receivedAllMessagesInOrder.set(false);
				throw new MessageDeliveredOutOfOrderException("Received message: " + message.getTimestamp() + ", but expected: " + lastReceivedTimestamp);
			}
			
			lastReceivedTimestamp.set(timestamp.getTimestamp());
			receivedMessages.add(message);
		}
	}
	
	public boolean receivedAllMessagesInOrder() {
		synchronized (lock) {
			return receivedAllMessagesInOrder.get();
		}
	}
	
	public List<Message> getReceivedMessages() {
		return Collections.unmodifiableList(receivedMessages);
	}
	
}

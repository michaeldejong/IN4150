package nl.tudelft.in4150.group18.implementation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nl.tudelft.in4150.group18.DistributedAlgorithmWithAcks;
import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;

/**
 * This class is implements the Total Ordering algorithm as described in the lectures.
 */
public class TotalOrdering extends DistributedAlgorithmWithAcks<Message, Ack> {

	private static final Logger log = LoggerFactory.getLogger(TotalOrdering.class);
	
	private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
	private final Object lock = new Object();

	private final Clock clock = new Clock();
	private final Multimap<MessageIdentifier, Address> receivedAcks = HashMultimap.create();
	private final Queue<Message> messageQueue = Queues.newLinkedBlockingQueue();
	private final MessageConsumer messageConsumer;
	
	public TotalOrdering(MessageConsumer messageConsumer) {
		this.messageConsumer = messageConsumer;
	}
	
	@Override
	public void start() {
		log.info("Starting algorithm...");
		
		Runnable broadcaster = new Runnable() {
			@Override
			public void run() {
				Message message = createMessage();
				log.info("Broadcasting message: {}", message);
				broadcast(message);
			}
		};
		
		executor.scheduleWithFixedDelay(broadcaster, 1000, 100, TimeUnit.MILLISECONDS);
	}

	@Override
	protected void onAcknowledgement(Ack message, Address from) {
		MessageIdentifier timestamp = message.getTimestamp();
		log.debug("Received ACK for message with timestamp: {}", timestamp);
		receivedAcks.put(timestamp, from);
		
		checkMessages();
	}

	@Override
	protected void onMessageReceived(Message message, Address from) {
		synchronized (lock) {
			log.debug("Received a Message {} from {} and placed it in the message queue", message, from);
			messageQueue.add(message);
			
			log.debug("Broadcasting ACK for message {} to the cluster", message);
			MessageIdentifier timestamp = message.getTimestamp();
			broadcast(new Ack(timestamp));
			
			clock.updateWithExternalTime(timestamp.getTimestamp());
			checkMessages();
		}
	}
	
	@Override
	protected void send(IMessage message, Address address) throws RemoteException {
		synchronized (lock) {
			super.send(message, address);
			clock.increment();
		}
	}
	
	@Override
	protected void multicast(IMessage message, Collection<Address> addresses) {
		synchronized (lock) {
			super.multicast(message, addresses);
			clock.increment();
		}
	}
	
	@Override
	protected void broadcast(IMessage message) {
		synchronized (lock) {
			super.broadcast(message);
			clock.increment();
		}
	}

	private void checkMessages() {
		log.debug("Checking if one or more Messages can be delivered...");
		
		while (!messageQueue.isEmpty()) {
			Message message = messageQueue.peek();
			MessageIdentifier timestamp = message.getTimestamp();
		
			if (!entireClusterAcknowledgedMessage(timestamp)) {
				return;
			}
			
			receivedAcks.removeAll(timestamp);
			messageConsumer.deliver(messageQueue.poll());
		}
	}

	private boolean entireClusterAcknowledgedMessage(MessageIdentifier timestamp) {
		return receivedAcks.get(timestamp).size() == (getRemoteAddresses().size() - 1);
	}

	private Message createMessage() {
		return new Message(new MessageIdentifier(clock.getTime(), getLocalAddress()));
	}
	
}

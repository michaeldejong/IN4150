package nl.tudelft.in4150.group18.implementation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nl.tudelft.in4150.group18.DistributedAlgorithmWithAcks;
import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;
import nl.tudelft.in4150.group18.utils.Clock;

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

	/**
	 * Incoming message queue
	 */
	private final Queue<Message> messageQueue = Queues.newPriorityQueue();
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

				int randomDelay = new Random().nextInt(100);
				executor.schedule(this, randomDelay, TimeUnit.MILLISECONDS);
			}
		};

		executor.submit(broadcaster);
	}

	@Override
	protected void onAcknowledgement(Ack message, Address from) {
		MessageIdentifier timestamp = message.getId();
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
			MessageIdentifier timestamp = message.getId();
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

	/**
	 * Either the message queue is empty or we have to wait for others to ACK (-> return;)
	 */
	private void checkMessages() {
		log.debug("Checking if one or more Messages can be delivered...");

		while (!messageQueue.isEmpty()) {
			Message message = messageQueue.peek(); // oldest message
			MessageIdentifier messageId = message.getId();

			if (!entireClusterAcknowledgedMessage(messageId)) {
				return;
			}

			receivedAcks.removeAll(messageId);
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

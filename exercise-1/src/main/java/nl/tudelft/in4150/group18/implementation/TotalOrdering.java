package nl.tudelft.in4150.group18.implementation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.PriorityQueue;
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

	private final ScheduledThreadPoolExecutor executor;
	private final Multimap<MessageIdentifier, Address> receivedAcks;
	private final PriorityQueue<Message> messageQueue;
	private final MessageConsumer messageConsumer;
	private final Clock clock;
	
	private final Object lock = new Object();

	/**
	 * Constructs a new {@link TotalOrdering} object.
	 * 
	 * @param messageConsumer	The {@link MessageConsumer} to deliver messages to.
	 */
	public TotalOrdering(MessageConsumer messageConsumer) {
		this.executor = new ScheduledThreadPoolExecutor(1);
		this.receivedAcks = HashMultimap.create();
		this.messageQueue = Queues.newPriorityQueue();
		this.messageConsumer = messageConsumer;
		this.clock = new Clock();
	}

	/**
	 * This method is called by a user to trigger the execution of this algorithm.
	 */
	@Override
	public void start() {
		log.info(getLocalAddress() + " - Starting algorithm...");

		Runnable broadcaster = new Runnable() {
			@Override
			public void run() {
				Message message = createMessage();
				log.info(getLocalAddress() + " - Broadcasting message: {}", message);
				broadcast(message);

				int randomDelay = new Random().nextInt(100);
				executor.schedule(this, randomDelay, TimeUnit.MILLISECONDS);
			}
		};

		executor.submit(broadcaster);
	}

	/**
	 * This method handles ACKs which this node received from the cluster.
	 */
	@Override
	protected void onAcknowledgement(Ack message, Address from) {
		synchronized (lock) {
			MessageIdentifier messageId = message.getId();
			log.debug(getLocalAddress() + " - Received ACK for message with id: {}", messageId);
			receivedAcks.put(messageId, from);
	
			checkMessages();
		}
	}

	/**
	 * This method handles Messages which this node received from the cluster.
	 */
	@Override
	protected void onMessageReceived(Message message, Address from) {
		synchronized (lock) {
			log.debug(getLocalAddress() + " - Received a Message {} from {} and placed it in the message queue", message, from);
			
			try {
				messageQueue.add(message);
			}
			catch (Throwable e) {
				log.error(e.getMessage(), e);
				log.info("");
			}

			log.debug(getLocalAddress() + " - Broadcasting ACK for message {} to the cluster", message);
			MessageIdentifier messageId = message.getId();
			broadcast(new Ack(messageId));

			clock.updateWithExternalTime(messageId.getTimestamp());
			checkMessages();
		}
	}

	/**
	 * This method sends an object to another node in the cluster. Additionally it will update its internal clock.
	 */
	@Override
	protected void send(IMessage message, Address address) throws RemoteException {
		synchronized (lock) {
			super.send(message, address);
			clock.increment();
		}
	}

	/**
	 * This method multicasts an object to other nodes in the cluster. Additionally it will update its internal clock.
	 */
	@Override
	protected void multicast(IMessage message, Collection<Address> addresses) {
		synchronized (lock) {
			super.multicast(message, addresses);
			clock.increment();
		}
	}

	/**
	 * This method broadcasts an object to other nodes in the cluster. Additionally it will update its internal clock.
	 */
	@Override
	protected void broadcast(IMessage message) {
		synchronized (lock) {
			super.broadcast(message);
			clock.increment();
		}
	}

	/**
	 * This method will check if there are {@link Message}s in the message queue which can be 
	 * delivered to the {@link MessageConsumer}.
	 * 
	 * Messages are delivered if the entire cluster acknowledged the receipt of the {@link Message}, 
	 * and the {@link Message} is the oldest {@link Message} in the message queue.
	 * 
	 * If the queue is empty or no message can be delivered, this method will return normally.
	 */
	private void checkMessages() {
		log.debug(getLocalAddress() + " - Checking if one or more Messages can be delivered...");

		while (!messageQueue.isEmpty()) {
			Message message = messageQueue.peek(); // oldest message
			MessageIdentifier messageId = message.getId();

			if (!entireClusterAcknowledgedMessage(messageId)) {
				log.debug(getLocalAddress() + " - Missing one or more ACKs for messageId: {}", messageId);
				return;
			}

			receivedAcks.removeAll(messageId);
			deliverMessage(message);
		}
		
		log.debug(getLocalAddress() + " - Queue for messages is empty!");
	}

	/**
	 * This method will deliver a {@link Message} to the {@link MessageConsumer}. In case the {@link MessageConsumer}
	 * determines that the {@link Message} was not delivered in the correct order, it will throw a 
	 * {@link MessageDeliveredOutOfOrderException}. This {@link MessageDeliveredOutOfOrderException} will be logged, 
	 * and forcefully shutdown the node.
	 * 
	 * @param message	The {@link Message} to deliver.
	 */
	private void deliverMessage(Message message) {
		try {
			log.info(getLocalAddress() + " - Delivering message {} internally", message);
			messageConsumer.deliver(messageQueue.poll());
		}
		catch (MessageDeliveredOutOfOrderException e) {
			log.error("FATAL: " + e.getMessage(), e);
			System.exit(1);
		}
	}

	/**
	 * This method checks if a {@link Message} has been acknowledged by the entire cluster.
	 * 
	 * @param messageId		The {@link MessageIdentifier} of the {@link Message} to check.
	 * @return				True if everybody acknowledged the {@link Message}.
	 */
	private boolean entireClusterAcknowledgedMessage(MessageIdentifier messageId) {
		return receivedAcks.get(messageId).size() == (getRemoteAddresses().size() - 1);
	}

	/**
	 * @return	A new {@link Message} with a new {@link MessageIdentifier}.
	 */
	private Message createMessage() {
		return new Message(new MessageIdentifier(clock.getTime(), getLocalAddress()));
	}

}

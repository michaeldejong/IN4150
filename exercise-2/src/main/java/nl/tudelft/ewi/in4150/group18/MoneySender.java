package nl.tudelft.ewi.in4150.group18;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import nl.tudelft.in4150.group18.network.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public abstract class MoneySender implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(MoneySender.class);

	private final AtomicLong messageId = new AtomicLong(0);
	private final AtomicLong balance = new AtomicLong(1000);
	private final Random random = new Random(System.currentTimeMillis());
	private final List<Address> remotes;
	private volatile Thread thread;

	public MoneySender(Collection<Address> remotes) {
		this.remotes = Lists.newArrayList(remotes);
	}

	public void start() {
		if (thread != null) {
			throw new IllegalStateException("Cannot start RandomBroadcaster twice!");
		}
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		while (true) {
			if (balance.get() >= 100) {
				balance.addAndGet(-100);
				try {
					sendTransaction(new Transaction(messageId.getAndIncrement(), 100), selectRandomPeer());
				} catch (Throwable e) {
					log.warn(e.getMessage(), e);
					balance.addAndGet(100);
				}
			}

			try {
				Thread.sleep(random.nextInt(250));
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private Address selectRandomPeer() {
		return remotes.get(random.nextInt(remotes.size()));
	}

	abstract void sendTransaction(Transaction transaction, Address to) throws Throwable;

	public void receivedTransaction(Transaction transaction) {
		balance.addAndGet(transaction.getValue());
	}

	public long getBalance() {
		return balance.get();
	}
}

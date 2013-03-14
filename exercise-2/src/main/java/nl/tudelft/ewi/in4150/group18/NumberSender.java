package nl.tudelft.ewi.in4150.group18;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import nl.tudelft.in4150.group18.network.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public abstract class NumberSender implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(NumberSender.class);

	private final AtomicBoolean send = new AtomicBoolean(true);
	private final AtomicLong messageId = new AtomicLong(0);
	private final Random random = new Random(System.currentTimeMillis());
	private final List<Address> remotes;
	private volatile Thread thread;

	public NumberSender(Collection<Address> remotes) {
		this.remotes = Lists.newArrayList(remotes);
	}

	public void start() {
		if (thread != null) {
			throw new IllegalStateException("Cannot start NumberSender twice!");
		}
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				while (!send.get()) {
					Thread.sleep(100);
				}
				
				sendTransaction(new Message(messageId.getAndIncrement()), selectRandomPeer());
				Thread.sleep(random.nextInt(1000));
			} catch (Throwable e) {
				log.warn(e.getMessage(), e);
			}
		}
	}

	private Address selectRandomPeer() {
		return remotes.get(random.nextInt(remotes.size()));
	}

	abstract void sendTransaction(Message transaction, Address to) throws Throwable;

	public void resume() {
		send.set(true);
	}

	public void pause() {
		send.set(false);
	}

	public long getId() {
		return messageId.get();
	}

}

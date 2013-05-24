package nl.tudelft.ewi.in4150.group18;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.DistributedAlgorithm;
import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ByzantineAgreement extends DistributedAlgorithm {

	private final Map<Address, Type> received = Maps.newHashMap();
	private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
	private final AtomicReference<Future<?>> currentFuture = new AtomicReference<>();
	private final Type defaultCommand;
	
	private int timeout = 100;
	private int maximumFaults = 1;

	public ByzantineAgreement(Type defaultCommand) {
		this.defaultCommand = defaultCommand;
	}

	public void setMaximumFaults(int maximumFaults) {
		this.maximumFaults = maximumFaults;
	}
	
	public void setTimeout(int millis) {
		this.timeout = millis;
	}

	@Override
	public void start() {
		broadcastWait(new Command(maximumFaults, defaultCommand, Lists.newArrayList(getLocalAddress())));
	}

	@Override
	public void onMessage(IMessage message, Address from) {
		if (message instanceof Command) {
			time();
			handleCommand((Command) message, from);
		}
	}

	/**
	 * Algorithm OM(0). 
	 * (1) The commander sends his value to every lieutenant. 
	 * (2) Each lieutenant uses the value he receives from the commander, or uses the value RETREAT if he receives no value. 
	 * 
	 * Algorithm OM(m), m > O. 
	 * (1) The commander sends his value to every lieutenant. 
	 * 
	 * (2) For each i, let vi be the value Lieutenant i receives from the commander, or else be RETREAT if he receives no value.
	 * Lieutenant i acts as the commander in Algorithm OM(m - 1) to send the value vi to each of the n - 2 other lieutenants.
	 * 
	 * (3) For each i, and each j != i, let vj be the value Lieutenant i received from Lieutenant j in step (2)
	 * (using Algorithm OM(m - 1)), or else RETREAT if he received no such value.
	 * Lieutenant i uses the value majority (v1 ..... vn-1 ). 
	 * 
	 * source: http://www.cs.cornell.edu/courses/cs614/2004sp/papers/lsp82.pdf
	 * 
	 * @param message	the {@link Command} the lieutenant receives from the commander.
	 * @param from		the {@link Address} he receives it from (commander).
	 */
	private void handleCommand(Command message, Address from) {
		received.put(from, message.getType());
		
		Set<Address> remaining = Sets.newHashSet(getRemoteAddresses());
		remaining.removeAll(message.getPath());
		
		List<Address> path = Lists.newArrayList(message.getPath());
		path.add(getLocalAddress());
		
		if (message.getMaximumFaults() > 0){
			int maximumFaults = message.getMaximumFaults() - 1;
			Type content = message.getType();

			multicastWait(new Command(maximumFaults, content, path), remaining);
		}
	}

	private void time() {
		synchronized (currentFuture) {
			if (currentFuture.get() != null) {
				currentFuture.get().cancel(true);
			}
			
			currentFuture.set(executor.schedule(new Runnable() {
				@Override
				public void run() {
					for (Address address : getRemoteAddresses()) {
						if (!received.containsKey(address)) {
							received.put(address, defaultCommand);
						}
					}
					
					checkMajority();
				}
			}, timeout, TimeUnit.MILLISECONDS));
		}
	}

	private void checkMajority() {
		if (received.size() < getRemoteAddresses().size()) {
			return;
		}
		
		int attack = 0;
		int retreat = 0;
		for (Type type : received.values()) {
			if (type == Type.ATTACK) {
				attack++;
			}
			else if (type == Type.RETREAT) {
				retreat++;
			}
		}
		
		Type majority = attack > retreat ? Type.ATTACK : Type.RETREAT;
		System.out.println(getLocalAddress() + ": (" + getClass().getSimpleName() + ") - I decided to: " + majority + " (" + attack + "A / " + retreat + "R)");
	}
	
}

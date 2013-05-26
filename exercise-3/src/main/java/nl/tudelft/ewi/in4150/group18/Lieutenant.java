package nl.tudelft.ewi.in4150.group18;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.SynchronousDistributedAlgorithm;
import nl.tudelft.in4150.group18.common.IRemoteRequest.IRequest;
import nl.tudelft.in4150.group18.network.Address;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Lieutenant extends SynchronousDistributedAlgorithm<Type> {

	private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
	private final AtomicBoolean startedProcess = new AtomicBoolean();
	private final ReceivedCommandsTracker tracker = new ReceivedCommandsTracker();
	
	private final Type defaultCommand;
	
	private int maximumFaults = 1;
	private int decisionTimeout = 1000;
	private int timeout = 100;

	public Lieutenant(Type defaultCommand) {
		this.defaultCommand = defaultCommand;
	}

	public void setMaximumFaults(int maximumFaults) {
		this.maximumFaults = maximumFaults;
	}
	
	public void setTimeout(int millis) {
		this.timeout = millis;
	}
	
	public int getTimeout() {
		return timeout;
	}

	@Override
	public void start() {
		System.err.println(getLocalAddress() + " - (COMMANDER) - I'm ordering: " + defaultCommand);
		broadcast(new Command(maximumFaults, defaultCommand, Lists.newArrayList(getLocalAddress())), timeout, defaultCommand);
	}

	@Override
	public Type onRequest(IRequest message, Address from) {
		if (message instanceof Command) {
			startDecisionTimer();
			return handleCommand((Command) message, from);
		}
		return null;
	}

	private void startDecisionTimer() {
		if (!startedProcess.compareAndSet(false, true)) {
			return;
		}
		
		executor.schedule(new Runnable() {
			@Override
			public void run() {
				printDecision();
			}
		}, decisionTimeout, TimeUnit.MILLISECONDS);
	}

	protected void printDecision() {
		System.err.println(getLocalAddress() + " - (" + getClass().getSimpleName() + ") - I decided to: " 
				+ tracker.decide() + " - (A:" + tracker.count(Type.ATTACK) + "/R:" + tracker.count(Type.RETREAT) + ")");
		
		tracker.clear();
		startedProcess.set(false);
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
	protected Type handleCommand(Command message, Address from) {
		tracker.processCommand(message, from);
		
		List<Address> path = Lists.newArrayList();
		path.addAll(message.getPath());
		path.add(getLocalAddress());
		
		Set<Address> remaining = Sets.newHashSet();
		remaining.addAll(getRemoteAddresses());
		remaining.removeAll(message.getPath());
		remaining.remove(getLocalAddress());
		
		if (message.getMaximumFaults() > 0){
			int maximumFaults = message.getMaximumFaults() - 1;
			Type content = message.getType();

			Map<Address, Type> responses = multicast(new Command(maximumFaults, content, path), remaining, timeout, defaultCommand);
			responses.put(from, message.getType());
			return majority(responses);
		}

		return message.getType();
	}

	protected Type majority(Map<Address, Type> responses) {
		int attack = 0;
		int retreat = 0;
		for (Type type : responses.values()) {
			if (type == Type.ATTACK) {
				attack++;
			}
			else if (type == Type.RETREAT) {
				retreat++;
			}
		}
		return attack >= retreat ? Type.ATTACK : Type.RETREAT;
	}

	protected String enumerate(Map<Address, Type> responses) {
		int attack = 0;
		int retreat = 0;
		for (Type type : responses.values()) {
			if (type == Type.ATTACK) {
				attack++;
			}
			else if (type == Type.RETREAT) {
				retreat++;
			}
		}
		return "A: " + attack + "/R: " + retreat;
	}

}

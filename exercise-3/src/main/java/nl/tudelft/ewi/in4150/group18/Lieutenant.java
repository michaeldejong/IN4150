package nl.tudelft.ewi.in4150.group18;

import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.SynchronousDistributedAlgorithm;
import nl.tudelft.in4150.group18.common.IRemoteRequest.IRequest;
import nl.tudelft.in4150.group18.network.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Lieutenant extends SynchronousDistributedAlgorithm<Type> {

	private static final Logger log = LoggerFactory.getLogger(Lieutenant.class);
	
	private final Type defaultCommand;
	private final Collector collector;
	
	private int maximumFaults = 1;
	private int timeout = 60*1000;

	public Lieutenant(Type defaultCommand) {
		this.defaultCommand = defaultCommand;
		this.collector = new Collector(getClass());
	}
	
	@Override
	public void start() {
		resetSentMessages();
		log.info(getLocalAddress() + " - (COMMANDER) - I'm ordering: " + defaultCommand);
		broadcast(new Command(maximumFaults, defaultCommand, Lists.newArrayList(getLocalAddress())), timeout, defaultCommand);
		log.info("A total of {} messages was sent!", getSentMessages());
	}

	@Override
	public Type onRequest(IRequest message, Address from) {
		if (message != null && message instanceof Command) {
			return handleCommand((Command) message, from);
		}
		return null;
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
	 * @param message	the {@link Command} the lieutenant receives from the commander.
	 * @param from		the {@link Address} he receives it from (commander).
	 * @throws InterruptedException 
	 */
	protected Type handleCommand(Command message, Address from) {
		collector.collect(getLocalAddress(), message.getType(), message.getPath());
		
		// Add self to path
		List<Address> path = Lists.newArrayList(message.getPath());
		path.add(getLocalAddress());
		
		// Calculate remaining nodes
		Set<Address> remaining = Sets.newHashSet();
		remaining.addAll(getRemoteAddresses());
		remaining.removeAll(message.getPath());
		remaining.remove(getLocalAddress());
		
		Type order = message.getType();
		if (message.getMaximumFaults() > 0){
			int maximumFaults = message.getMaximumFaults() - 1;
			Type content = message.getType();
			
			int modifiedTimeout = (int) (timeout * Math.pow(0.5, path.size()));
			Map<Address, Type> responses = multicast(new Command(maximumFaults, content, path), remaining, modifiedTimeout, defaultCommand);
			responses.put(from, message.getType());
			order = majority(responses, message.getType());
			collector.collect(getLocalAddress(), order, path);
		}
		return order;
	}

	public void setMaximumFaults(int maximumFaults) {
		this.maximumFaults = maximumFaults;
	}
	
	public int getMaximumFaults() {
		return maximumFaults;
	}
	
	private Type majority(Map<Address, Type> responses, Type ordered) {
		int attack = count(responses, Type.ATTACK);
		int retreat = count(responses, Type.RETREAT);
		if (attack == retreat) {
			return ordered;
		}
		return attack > retreat ? Type.ATTACK : Type.RETREAT;
	}

	private int count(Map<?, Type> responses, Type needle) {
		int count = 0;
		for (Type type : responses.values()) {
			if (type == needle) {
				count++;
			}
		}
		return count;
	}

}

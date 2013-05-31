package nl.tudelft.ewi.in4150.group18;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.network.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Traitor extends Lieutenant {
	
	private static final Logger log = LoggerFactory.getLogger(Traitor.class);

	public Traitor(Type defaultCommand) {
		super(defaultCommand);
	}
	
	@Override
	public void start() {
		log.info(getLocalAddress() + " - (COMMANDER) - I'm ordering: random commands...");
		try {
			randomizedBroadcast();
		} catch (RemoteException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	protected Type handleCommand(Command message, Address from) {
		List<Address> path = Lists.newArrayList();
		path.addAll(message.getPath());
		path.add(getLocalAddress());
		
		Set<Address> remaining = Sets.newHashSet();
		remaining.addAll(getRemoteAddresses());
		remaining.removeAll(message.getPath());
		remaining.remove(getLocalAddress());
		
		Type order = message.getType().opposite();
		if (message.getMaximumFaults() > 0){
			int maximumFaults = message.getMaximumFaults() - 1;
			multicast(new Command(maximumFaults, order, path), remaining, getTimeout(), null);
		}
		
		return order;
	}
	
	private void randomizedBroadcast() throws RemoteException {
		for (Address address : getRemoteAddresses()) {
			Type order = Math.random() < 0.5 ? Type.ATTACK : Type.RETREAT;
			log.info("Sending command: {} to: {}", order, address);
			send(new Command(getMaximumFaults(), order, Lists.newArrayList(getLocalAddress())), address);
		}
	}
	
}

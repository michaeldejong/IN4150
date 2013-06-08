package nl.tudelft.ewi.in4150.group18;

import java.rmi.RemoteException;
import java.util.ArrayList;
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
			multicastWrongValues(maximumFaults, path, remaining);
		}
		
		return order;
	}
	
	private void randomizedBroadcast() throws RemoteException {
		Type send = Type.ATTACK;
		ArrayList<Address> path = Lists.newArrayList(getLocalAddress());
		
		for (Address address : getRemoteAddresses()) {
			sendAwait(new Command(getMaximumFaults(), send, path), address);
			send = send.opposite();
		}
	}
	
	private void multicastWrongValues(int maxFaults, List<Address> path, Set<Address> remaining) {
		for (Address address : remaining) {
			try {
				Type send = address.getPort() % 2 == 1 ? Type.ATTACK : Type.RETREAT;
				sendAwait(new Command(maxFaults, send, path), address);
			} catch (RemoteException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
}

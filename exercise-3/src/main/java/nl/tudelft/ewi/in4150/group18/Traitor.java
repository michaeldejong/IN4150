package nl.tudelft.ewi.in4150.group18;

import java.util.List;
import java.util.Set;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.network.Address;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Traitor extends Lieutenant {

	public Traitor(Type defaultCommand) {
		super(defaultCommand);
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
		
		if (message.getMaximumFaults() > 0){
			int maximumFaults = message.getMaximumFaults() - 1;
			multicast(new Command(maximumFaults, Type.RETREAT, path), remaining, getTimeout(), null);
		}
		
		return Type.RETREAT;
	}
	
}

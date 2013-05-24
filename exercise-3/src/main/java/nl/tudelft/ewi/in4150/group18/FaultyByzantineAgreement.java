package nl.tudelft.ewi.in4150.group18;

import java.util.List;
import java.util.Random;
import java.util.Set;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class FaultyByzantineAgreement extends ByzantineAgreement {

	public FaultyByzantineAgreement(Type defaultCommand) {
		super(defaultCommand);
	}

	@Override
	public void onMessage(IMessage message, Address from) {
		if (message instanceof Command) {
			Command command = (Command) message;
			int maximumFaults = command.getMaximumFaults() - 1;
			Type content = command.getType();
			
			Set<Address> remaining = Sets.newHashSet(getRemoteAddresses());
			List<Address> path = Lists.newArrayList(command.getPath());
			
			if (Math.random() < 0.25) { // crash
				return;
			}
			
			if (Math.random() < 0.25) { // screw up the f value
				maximumFaults *= new Random().nextInt(10);
			}
			
			if (Math.random() < 0.5) { // forget content
				content = null;
			} 
			else if (Math.random() < 0.5) { // pick something at random
				if (Math.random() < 0.5) {
					content = Type.ATTACK;
				} else {
					content = Type.RETREAT;
				}
			}
			
			// randomize path
			for (int i = path.size(); i >= 0; i--) {
				if (Math.random() < 0.5) {
					path.set(i, path.get((int) (Math.random() * path.size())));
				}
			}
			
			// forget addresses
			for (Address address : remaining) {
				if (Math.random() < 0.5) {
					remaining.remove(address);
				}
			}
			
			super.onMessage(new Command(maximumFaults, content, path), from);
		}
	}
	
}

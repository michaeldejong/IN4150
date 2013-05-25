package nl.tudelft.ewi.in4150.group18;

import java.util.Random;
import java.util.Set;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.common.IRemoteRequest.IRequest;
import nl.tudelft.in4150.group18.network.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Faulty extends Lieutenant {

	private static final Logger log = LoggerFactory.getLogger(Faulty.class);
	
	public Faulty(Type defaultCommand) {
		super(defaultCommand);
	}

	@Override
	public Type onRequest(IRequest message, Address from) {
		if (message instanceof Command) {
			Command command = (Command) message;
			int maximumFaults = command.getMaximumFaults() - 1;
			Set<Address> remaining = command.getRemaining();
			Type content = command.getType();
			
			if (Math.random() < 0.25) { // crash
				try {
					Thread.sleep(Integer.MAX_VALUE);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
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
			
			// forget addresses
			for (Address address : remaining) {
				if (Math.random() < 0.5) {
					remaining.remove(address);
				}
			}
			
			return super.onRequest(new Command(maximumFaults, content, remaining), from);
		}
		return null;
	}
	
}

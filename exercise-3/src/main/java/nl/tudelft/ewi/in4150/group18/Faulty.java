package nl.tudelft.ewi.in4150.group18;

import java.util.List;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.common.IRemoteRequest.IRequest;
import nl.tudelft.in4150.group18.network.Address;

public class Faulty extends Lieutenant {

	public Faulty(Type defaultCommand) {
		super(defaultCommand);
	}

	@Override
	public Type onRequest(IRequest message, Address from) {
		if (message instanceof Command) {
			Command command = (Command) message;
			int maximumFaults = command.getMaximumFaults();
			List<Address> path = command.getPath();
			
			if (Math.random() < 0.4) {
				maximumFaults--;
			}
			
			if (Math.random() < 0.4) {
				path.remove(0);
			}
			
			Type content = command.getType();
			
			if (Math.random() < 0.50) { // reverse order
				content = command.getType().opposite();
			} 
			
			if (Math.random() < 0.1) {
				return super.onRequest(new Command(maximumFaults, content, path), from);
			}
		}
		return null;
	}
	
}

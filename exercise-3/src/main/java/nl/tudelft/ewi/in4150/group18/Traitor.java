package nl.tudelft.ewi.in4150.group18;

import java.util.Set;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.common.IRemoteRequest.IRequest;
import nl.tudelft.in4150.group18.network.Address;

public class Traitor extends Lieutenant {

	public Traitor(Type defaultCommand) {
		super(defaultCommand);
	}

	@Override
	public Type onRequest(IRequest message, Address from) {
		if (message instanceof Command) {
			Command command = (Command) message;
			int maximumFaults = command.getMaximumFaults() - 1;
			Set<Address> remaining = command.getRemaining();
			Type content = command.getType();
			
			if (content.equals(Type.ATTACK)) {
				content = Type.RETREAT;
			} 
			else {
				content = Type.ATTACK;
			}
			
			return super.onRequest(new Command(maximumFaults, content, remaining), from);
		}
		return null;
	}
	
}

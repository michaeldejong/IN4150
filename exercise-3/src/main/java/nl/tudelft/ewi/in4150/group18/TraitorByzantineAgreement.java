package nl.tudelft.ewi.in4150.group18;

import java.util.List;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

import com.google.common.collect.Lists;

public class TraitorByzantineAgreement extends ByzantineAgreement {

	public TraitorByzantineAgreement(Type defaultCommand) {
		super(defaultCommand);
	}

	@Override
	public void onMessage(IMessage message, Address from) {
		if (message instanceof Command) {
			Command command = (Command) message;
			int maximumFaults = command.getMaximumFaults() - 1;
			Type content = command.getType();
			
			List<Address> path = Lists.newArrayList(command.getPath());
			
			if (content.equals(Type.ATTACK)) {
				content = Type.RETREAT;
			} 
			else {
				content = Type.ATTACK;
			}
			
			super.onMessage(new Command(maximumFaults, content, path), from);
		}
	}
	
}

package nl.tudelft.ewi.in4150.group18;

import java.util.List;
import java.util.Set;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.DistributedAlgorithm;
import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ByzantineAgreement extends DistributedAlgorithm {

	private Type command;
	private int f;

	public void configureCommander(Type command, int f) {
		this.command = command;
		this.f = f;
	}
	
	@Override
	public void start() {
		broadcastSynchronous(new Command(f, command, Lists.newArrayList(getLocalAddress())));
	}

	@Override
	public void onMessage(IMessage message, Address from) {
		if (message instanceof Command) {
			handleCommand((Command) message, from);
		}
	}

	private void handleCommand(Command message, Address from) {
		Set<Address> remaining = Sets.newHashSet(getRemoteAddresses());
		remaining.removeAll(message.getPath());

		if (message.getF() == 0) {
			// Do something magical...
		}
		else {
			List<Address> path = Lists.newArrayList(message.getPath());
			path.add(getLocalAddress());
			
			multicastSynchronous(new Command(message.getF() - 1, message.getType(), path), remaining);
		}
	}

}

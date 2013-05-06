package nl.tudelft.ewi.in4150.group18;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.DistributedAlgorithm;
import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

public class ByzantineAgreement extends DistributedAlgorithm {

	private Type command;
	private int f;

	public void configureCommander(Type command, int f) {
		this.command = command;
		this.f = f;
	}
	
	@Override
	public void start() {
		broadcastSynchronous(new Command(f, command, getRemoteAddresses()));
	}

	@Override
	public void onMessage(IMessage message, Address from) {
		

	}

}

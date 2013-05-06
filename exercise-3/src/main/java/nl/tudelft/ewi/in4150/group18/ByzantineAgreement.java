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

	/**
	 * Algorithm OM(0). 
	 * (1) The commander sends his value to every lieutenant. 
	 * (2) Each lieutenant uses the value he receives from the commander, or uses the value RETREAT if he receives no value. 
	 * 
	 * Algorithm OM(m), m > O. 
	 * (1) The commander sends his value to every lieutenant. 
	 * 
	 * (2) For each i, let vi be the value Lieutenant i receives from the commander, or else be RETREAT if he receives no value.
	 * Lieutenant i acts as the commander in Algorithm OM(m - 1) to send the value vi to each of the n - 2 other lieutenants.
	 * 
	 * (3) For each i, and each j != i, let vj be the value Lieutenant i received from Lieutenant j in step (2)
	 * (using Algorithm OM(m - 1)), or else RETREAT if he received no such value.
	 * Lieutenant i uses the value majority (v1 ..... v,-1 ). 
	 * 
	 * source: http://www.cs.cornell.edu/courses/cs614/2004sp/papers/lsp82.pdf
	 * 
	 * @param message
	 * @param from
	 */
	private void handleCommand(Command message, Address from) {
		Set<Address> remaining = Sets.newHashSet(getRemoteAddresses());
		remaining.removeAll(message.getPath());

		if (message.getF() == 0) {
			// Do something magical...
		} else {
			List<Address> path = Lists.newArrayList(message.getPath());
			path.add(getLocalAddress());

			multicastSynchronous(new Command(message.getF() - 1, message.getType(), path), remaining);
		}
	}

}

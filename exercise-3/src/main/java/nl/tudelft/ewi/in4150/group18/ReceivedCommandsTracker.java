package nl.tudelft.ewi.in4150.group18;

import java.util.concurrent.ConcurrentMap;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.network.Address;

import com.google.common.collect.Maps;

public class ReceivedCommandsTracker {
	
	private final ConcurrentMap<Address, Command> receivedCommands = Maps.newConcurrentMap();
	
	public void processCommand(Command command, Address from) {
		if (command.getMaximumFaults() == 0 || !receivedCommands.containsKey(from)) {
			receivedCommands.put(from, command);
		}
	}
	
	public Type getCommandersOrder() {
		int count = 0;
		Type order = null;
		for (Command command : receivedCommands.values()) {
			if (command.getMaximumFaults() > count) {
				count = command.getMaximumFaults();
				order = command.getType();
			}
		}
		return order;
	}
	
	public int count(Type type) {
		int count = 0;
		for (Command command : receivedCommands.values()) {
			if (type == command.getType()) {
				count++;
			}
		}
		return count;
	}

	public Type decide() {
		int attack = count(Type.ATTACK);
		int retreat = count(Type.RETREAT);
		
		if (attack == retreat) {
			Type commandersOrder = getCommandersOrder();
			if (commandersOrder != null) {
				return commandersOrder;
			}
			return Type.RETREAT;
		}
		
		return attack > retreat ? Type.ATTACK : Type.RETREAT;
	}
	
	public void clear() {
		receivedCommands.clear();
	}

}

package nl.tudelft.ewi.in4150.group18;

import java.util.Set;

import nl.tudelft.in4150.group18.common.IRemoteMessage.IMessage;
import nl.tudelft.in4150.group18.common.IRemoteRequest.IRequest;
import nl.tudelft.in4150.group18.network.Address;

import com.google.common.base.Joiner;

public class Command implements IMessage, IRequest {

	private static final long serialVersionUID = 5523621758360730807L;
	public static final Type DEFAULT = Type.RETREAT;

	private int maximumFaults;
	private Type type;
	private Set<Address> remainingLieutenants;

	public Command(int maximumFaults, Type type, Set<Address> remainingLieutenants) {
		this.maximumFaults = maximumFaults;
		this.type = type;
		this.remainingLieutenants = remainingLieutenants;
	}

	public int getMaximumFaults() {
		return maximumFaults;
	}

	public Type getType() {
		return type;
	}

	public Set<Address> getRemaining() {
		return remainingLieutenants;
	}
	
	@Override
	public String toString() {
		return "[" + maximumFaults + ", " + type + ", [" + Joiner.on(",").join(remainingLieutenants) + "]]";
	}
	
	public enum Type {
		ATTACK, RETREAT;
	}

}

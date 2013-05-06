package nl.tudelft.ewi.in4150.group18;

import java.util.Collection;

import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

public class Command implements IMessage {

	private static final long serialVersionUID = 5523621758360730807L;

	private int f;
	private Type type;
	private Collection<Address> remainingLuitenants;
	
	public Command(int f, Type type, Collection<Address> remainingLuitenants) {
		this.f = f;
		this.type = type;
		this.remainingLuitenants = remainingLuitenants;
	}
	
	public int getF() {
		return f;
	}
	
	public Type getType() {
		return type;
	}
	
	public Collection<Address> getRemainingLuitenants() {
		return remainingLuitenants;
	}
	
	public enum Type {
		ATTACK, RETREAT;
	}

}

package nl.tudelft.ewi.in4150.group18;

import java.util.List;

import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

public class Command implements IMessage {

	private static final long serialVersionUID = 5523621758360730807L;
	public static final Type DEFAULT = Type.RETREAT;

	private int f;
	private Type type;
	private List<Address> path;

	public Command(int f, Type type, List<Address> path) {
		this.f = f;
		this.type = type;
		this.path = path;
	}

	public int getF() {
		return f;
	}

	public Type getType() {
		return type;
	}

	public List<Address> getPath() {
		return path;
	}

	public enum Type {
		ATTACK, RETREAT;
	}

}

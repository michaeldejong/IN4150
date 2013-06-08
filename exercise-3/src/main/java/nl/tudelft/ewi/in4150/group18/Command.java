package nl.tudelft.ewi.in4150.group18;

import java.util.List;

import nl.tudelft.in4150.group18.common.IRemoteMessage.IMessage;
import nl.tudelft.in4150.group18.common.IRemoteRequest.IRequest;
import nl.tudelft.in4150.group18.network.Address;

import com.google.common.base.Joiner;

public class Command implements IMessage, IRequest {

	private static final long serialVersionUID = 5523621758360730807L;
	public static final Type DEFAULT = Type.RETREAT;

	private int maximumFaults;
	private Type type;
	private List<Address> path;

	public Command(int maximumFaults, Type type, List<Address> path) {
		if (path.isEmpty()) {
			System.err.println();
		}
		this.maximumFaults = maximumFaults;
		this.type = type;
		this.path = path;
	}

	public int getMaximumFaults() {
		return maximumFaults;
	}

	public Type getType() {
		return type;
	}

	public List<Address> getPath() {
		return path;
	}
	
	@Override
	public String toString() {
		return "[" + maximumFaults + ", " + type + ", [" + Joiner.on(",").join(path) + "]]";
	}
	
	public enum Type {
		ATTACK {
			public Type opposite() {
				return RETREAT;
			}
		}, 
		RETREAT {
			public Type opposite() {
				return ATTACK;
			}
		};

		public abstract Type opposite();
	}

}

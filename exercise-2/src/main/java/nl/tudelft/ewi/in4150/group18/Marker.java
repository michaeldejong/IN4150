package nl.tudelft.ewi.in4150.group18;

import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

/**
 * A simple {@link IMessage} which acts as a channel marker.
 */
public class Marker implements IMessage {

	private static final long serialVersionUID = 6254433383721144594L;

	private Address sender;

	public Marker(Address sender) {
		this.sender = sender;
	}

	public Address getAddressSender() {
		return sender;
	}
}
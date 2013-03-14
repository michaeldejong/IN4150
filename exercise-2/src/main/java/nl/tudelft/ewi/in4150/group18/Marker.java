package nl.tudelft.ewi.in4150.group18;

import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;

/**
 * A simple {@link IMessage} which acts as a channel marker.
 */
public class Marker implements IMessage {

	private static final long serialVersionUID = 6254433383721144594L;
	
	@Override
	public String toString() {
		return "MARKER";
	}

}
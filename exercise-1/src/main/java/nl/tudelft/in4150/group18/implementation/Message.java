package nl.tudelft.in4150.group18.implementation;

import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;

/**
 * A simple {@link IMessage} containing a counter field.
 */
public class Message implements IMessage {

	private static final long serialVersionUID = 3585089413268308745L;
	
	private final MessageIdentifier timestamp;
	
	public Message(MessageIdentifier timestamp) {
		this.timestamp = timestamp;
	}
	
	public MessageIdentifier getTimestamp() {
		return timestamp;
	}
	
	@Override
	public int hashCode() {
		return timestamp.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Message) {
			Message o = (Message) other;
			return timestamp.equals(o.timestamp);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[" + timestamp + "]";
	}
	
}
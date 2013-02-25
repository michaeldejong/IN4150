package nl.tudelft.in4150.group18.implementation;

import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;

/**
 * A simple {@link IMessage} containing a counter field.
 */
public class Message implements IMessage {

	private static final long serialVersionUID = 3585089413268308745L;
	
	private final MessageIdentifier messageId;
	
	public Message(MessageIdentifier id) {
		this.messageId = id;
	}
	
	public MessageIdentifier getId() {
		return messageId;
	}
	
	@Override
	public int hashCode() {
		return messageId.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Message) {
			Message o = (Message) other;
			return messageId.equals(o.messageId);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[" + messageId + "]";
	}
	
}
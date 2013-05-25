package nl.tudelft.ewi.in4150.group18;

import nl.tudelft.in4150.group18.common.IRemoteMessage.IMessage;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A simple {@link IMessage} representing a money transfer
 */
public class Message implements IMessage, Comparable<Message> {

	private static final long serialVersionUID = -4315824567397173503L;
	
	private final long messageId;
	
	public Message(long id) {
		this.messageId = id;
	}
	
	public long getId() {
		return messageId;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(messageId).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Message) {
			Message o = (Message) other;
			return new EqualsBuilder().append(messageId, o.messageId).isEquals();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[" + messageId + "]";
	}

	@Override
	public int compareTo(Message o) {
		return Long.compare(messageId, o.messageId);
	}
	
}
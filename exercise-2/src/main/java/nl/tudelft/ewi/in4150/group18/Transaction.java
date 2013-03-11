package nl.tudelft.ewi.in4150.group18;

import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A simple {@link IMessage} representing a money transfer
 */
public class Transaction implements IMessage, Comparable<Transaction> {

	private static final long serialVersionUID = -4315824567397173503L;
	
	private final long messageId;
	private final long value; 
	
	public Transaction(long id, long value) {
		this.messageId = id;
		this.value = value;
	}
	
	public long getId() {
		return messageId;
	}
	
	public long getValue() {
		return value;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(messageId).append(value).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Transaction) {
			Transaction o = (Transaction) other;
			return new EqualsBuilder().append(messageId, o.messageId).append(value, o.value).isEquals();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[" + messageId + ", " + value + "]";
	}

	@Override
	public int compareTo(Transaction o) {
		return Long.compare(messageId, o.messageId);
	}
	
}
package nl.tudelft.in4150.group18;

import java.io.Serializable;

import nl.tudelft.in4150.group18.network.Address;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@SuppressWarnings("serial")
public class MessageIdentifier implements Serializable, Comparable<MessageIdentifier> {

	private final long timestamp;
	private final Address address;
	
	public MessageIdentifier(long timestamp, Address address) {
		this.timestamp = timestamp;
		this.address = address;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public Address getAddress() {
		return address;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(timestamp).append(address).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof MessageIdentifier) {
			MessageIdentifier o = (MessageIdentifier) other;
			return new EqualsBuilder().append(timestamp, o.timestamp).append(address, o.address).isEquals();
		}
		return false;
	}

	@Override
	public int compareTo(MessageIdentifier o) {
		int timestampCompare = Long.compare(timestamp, o.timestamp);
		if (timestampCompare != 0) {
			return timestampCompare;
		}
		
		return address.compareTo(o.address);
	}
	
	@Override
	public String toString() {
		return "[" + timestamp + ", " + address + "]";
	}
	
}

package nl.tudelft.ewi.in4150.group18;

import nl.tudelft.in4150.group18.common.IRemoteMessage.IMessage;
import nl.tudelft.in4150.group18.network.Address;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A simple {@link IMessage} which acts as a channel marker.
 */
public class Marker implements IMessage {

	private static final long serialVersionUID = 6254433383721144594L;
	
	private final Address address;
	private final long id;
	
	public Marker(Address address, long id) {
		this.address = address;
		this.id = id;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public long getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "MARKER[" + address + ", " + id + "]";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(address).append(id).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Marker) {
			Marker m = (Marker) other;
			return new EqualsBuilder().append(address, m.address).append(id, m.id).isEquals();
		}
		return false;
	}

}
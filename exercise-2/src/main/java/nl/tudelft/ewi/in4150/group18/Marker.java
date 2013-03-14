package nl.tudelft.ewi.in4150.group18;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

/**
 * A simple {@link IMessage} which acts as a channel marker.
 */
public class Marker implements IMessage {

	private static final long serialVersionUID = 6254433383721144594L;

	private Address origin;
	private long markerId;

	public Marker(Address origin, long markerId) {
		this.origin = origin;
		this.markerId = markerId;
	}

	public Address getOrigin() {
		return origin;
	}
	
	public long getMarkerId() {
		return markerId;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(origin).append(markerId).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Marker) {
			Marker o = (Marker) other;
			return new EqualsBuilder()
					.append(origin, o.origin)
					.append(markerId, o.markerId)
					.isEquals();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Marker[" + origin + ", " + markerId + "]";
	}
	
}
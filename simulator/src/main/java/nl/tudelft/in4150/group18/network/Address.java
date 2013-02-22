package nl.tudelft.in4150.group18.network;

import java.io.Serializable;

/**
 * This class is a data representation of a node's address.
 * 
 * @author michael
 */
public class Address implements Serializable {

	private static final long serialVersionUID = -6740148403370245172L;
	
	private final String hostAddress;
	private final int port;
	
	/**
	 * This constructs a new {@link Address}.
	 * 
	 * @param hostAddress	The IP address in {@link String} representation.
	 * @param port			The port of the node.
	 */
	public Address(String hostAddress, int port) {
		this.hostAddress = hostAddress;
		this.port = port;
	}
	
	/**
	 * @return	The IP address in {@link String} representation.
	 */
	public String getHostAddress() {
		return hostAddress;
	}
	
	/**
	 * @return	The port of the node.
	 */
	public int getPort() {
		return port;
	}
	
	@Override
	public String toString() {
		return "[" + hostAddress + ":" + port + "]";
	}
	
	@Override
	public int hashCode() {
		return hostAddress.hashCode() + port;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Address)) {
			return false;
		}
		Address otherAddress = ((Address) other);
		return hostAddress.equalsIgnoreCase(otherAddress.hostAddress) && port == otherAddress.port;
	}
	
}

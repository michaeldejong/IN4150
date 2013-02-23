package nl.tudelft.in4150.group18.network;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This class is a data representation of a node's address.
 * 
 * @author michael
 */
public class Address implements Serializable, Comparable<Address> {

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
		return new HashCodeBuilder().append(hostAddress).append(port).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Address) {
			Address o = (Address) other;
			return new EqualsBuilder().append(hostAddress, o.hostAddress).append(port, o.port).isEquals();
		}
		return false;
	}

	@Override
	public int compareTo(Address o) {
		byte[] myChunks = getChunks(hostAddress);
		byte[] otherChunks = getChunks(o.hostAddress);
		
		int lengthCompare = Integer.compare(myChunks.length, otherChunks.length);
		if (lengthCompare != 0) {
			return lengthCompare;
		}
		
		for (int i = 0; i < myChunks.length; i++) {
			int chunkCompare = Byte.compare(myChunks[i], otherChunks[i]);
			if (chunkCompare != 0) {
				return chunkCompare;
			}
		}
		
		return Integer.compare(port, o.port);
	}

	private byte[] getChunks(String address) {
		String[] chunks = address.split(".");
		byte[] result = new byte[chunks.length];
		for (int i = 0; i < chunks.length; i++) {
			result[i] = (byte) Integer.parseInt(chunks[i]);
		}
		return result;
	}
	
}

package nl.tudelft.in4150.group18.network;

import java.io.Serializable;

public class Address implements Serializable {

	private static final long serialVersionUID = -6740148403370245172L;
	
	private final String host;
	private final int port;
	
	public Address(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	@Override
	public String toString() {
		return "[" + host + ":" + port + "]";
	}
	
}

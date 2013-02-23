package nl.tudelft.in4150.group18;

public class Ack implements IAck {

	private static final long serialVersionUID = 1515323396408435666L;
	
	private final MessageIdentifier timestamp;
	
	public Ack(MessageIdentifier timestamp) {
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
		if (other instanceof Ack) {
			Ack o = (Ack) other;
			return timestamp.equals(o.timestamp);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[" + timestamp + "]";
	}

}

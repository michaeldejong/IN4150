package nl.tudelft.in4150.group18.implementation;

import nl.tudelft.in4150.group18.IAck;

public class Ack implements IAck {

	private static final long serialVersionUID = 1515323396408435666L;

	private final MessageIdentifier messageId;

	public Ack(MessageIdentifier id) {
		messageId = id;
	}

	/**
	 * @return original message id
	 */
	public MessageIdentifier getId() {
		return messageId;
	}

	@Override
	public int hashCode() {
		return messageId.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Ack) {
			Ack o = (Ack) other;
			return messageId.equals(o.messageId);
		}
		return false;
	}

	@Override
	public String toString() {
		return "[" + messageId + "]";
	}

}

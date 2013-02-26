package nl.tudelft.in4150.group18.implementation;

@SuppressWarnings("serial")
public class MessageDeliveredOutOfOrderException extends Exception {

	public MessageDeliveredOutOfOrderException(String message) {
		super(message);
	}
	
}

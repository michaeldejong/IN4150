package nl.tudelft.ewi.in4150.group18;

import java.io.Serializable;
import java.util.Map;

import nl.tudelft.in4150.group18.network.Address;

final class LocalState implements Serializable {

	private static final long serialVersionUID = 2676583448268213923L;

	private final int moneyValue;

	/** Id of last message sent per channel */
	private final Map<Address, Integer> sendMessageId;

	/** Id of last message received per channel */
	private final Map<Address, Integer> receivedMessageId;

	public LocalState(int moneyValue, Map<Address, Integer> sendMessageId, Map<Address, Integer> receivedMessageId) {
		this.moneyValue = moneyValue;
		this.sendMessageId = sendMessageId;
		this.receivedMessageId = receivedMessageId;
	}

	@Override
	public String toString() {
		// TODO

		return "";
	}

	public int getMoneyValue() {
		return moneyValue;
	}

	public int getSendMessageId(Address sendTo) {
		return sendMessageId.get(sendTo);
	}

	public int getReceivedMessageId(Address receivedFrom) {
		return receivedMessageId.get(receivedFrom);
	}
}

package nl.tudelft.ewi.in4150.group18;

import java.util.Map;

import nl.tudelft.in4150.group18.network.Address;

import com.google.common.collect.Maps;

final class LocalState {

	private final NumberSender moneySender;

	/** Id of last message sent per channel */
	private final Map<Address, Integer> sendMessageId;

	/** Id of last message received per channel */
	private final Map<Address, Integer> receivedMessageId;

	public LocalState(NumberSender moneySender) {
		this.moneySender = moneySender;
		this.sendMessageId = Maps.newHashMap();
		this.receivedMessageId = Maps.newHashMap();
	}

	@Override
	public String toString() {
		return "";
	}

	public int getSendMessageId(Address sendTo) {
		return sendMessageId.get(sendTo);
	}

	public int getReceivedMessageId(Address receivedFrom) {
		return receivedMessageId.get(receivedFrom);
	}
}

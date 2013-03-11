package nl.tudelft.ewi.in4150.group18;

import java.util.Map;
import java.util.Queue;

import nl.tudelft.in4150.group18.DistributedAlgorithm;
import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

public class ChandyLamportGlobalStateAlgorithm extends DistributedAlgorithm {

	/** Recorded local state per received marker */
	private final Map<Marker, LocalState> receivedMarkers = Maps.newHashMap();

	/** Queue of messages since last received marker */
	private final Map<Address, Queue<IMessage>> messageQueues = Maps.newHashMap();

	/** On arrival of a message or marker either save local state or process the message one at a time */
	private final Object lock = new Object();

	private volatile MoneySender sender;

	/** Check if the local process state is recorded for a certain marker (convenience method) */
	private boolean isStateRecorded(Marker m) {
		if (receivedMarkers.containsKey(m) && receivedMarkers.get(m) != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void start() {
		sender = new MoneySender(getRemoteAddresses()) {
			@Override
			void sendTransaction(Transaction transaction, Address to) throws Throwable {
				send(transaction, to);
			}
		};

		sender.start();
	}

	@Override
	public void onMessage(IMessage message, Address from) {
		synchronized (lock) {
			if (message instanceof Marker) {
				/**
					upon receipt of (a marker along channel c) do
						if (not local_state_recorded) then
							record state of c as empty
							record_local_state
						else
							record state of c as contents of Q(c)
				**/
				Marker marker = (Marker) message;
				if (!isStateRecorded(marker)) { // new marker received
					messageQueues.put(from, Queues.<IMessage> newArrayDeque());
					recordLocalState(marker);
				} else { // already received this marker
							// TODO record state of c as current content of Q(c).
				}
			}

			else if (message instanceof Transaction) {

				// TODO localStateRecorded not possible: per marker
				// TODO receivedMarkersFrom not possible: loop over all received marker.getAddress()
				if (localStateRecorded || receivedMarkersFrom.contains(from)) {
					messageQueues.get(from).add(message);
				} else {
					handleMessage(message);
				}
			}
		}
	}

	private void handleMessage(IMessage message) {
		// TODO Auto-generated method stub

	}

	/**
	procedure record_local_state:
		record local state
		local_state_recorded := true
		for (every outgoing channel c) do
			send(marker) along c
		for (every incoming channel c) do
			create message queue Q(c)
	 **/
	private void recordLocalState(Marker m) {
		synchronized (lock) {
			recordInternalState(m);

			broadcast(new Marker());

			messageQueues.clear();
			for (Address address : getRemoteAddresses()) {
				messageQueues.put(address, Queues.<IMessage> newArrayDeque());
			}
		}
	}

	private void recordInternalState(Marker m) {
		LocalState record = new LocalState(sender.getBalance(), // TODO last received and sent message id per address
				);

		receivedMarkers.put(m, record); // localStateRecorded = true;
	}
}

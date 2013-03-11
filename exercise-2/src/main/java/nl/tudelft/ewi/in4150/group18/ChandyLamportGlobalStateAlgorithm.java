package nl.tudelft.ewi.in4150.group18;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

import nl.tudelft.in4150.group18.DistributedAlgorithm;
import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;
import nl.tudelft.in4150.group18.network.Address;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

public class ChandyLamportGlobalStateAlgorithm extends DistributedAlgorithm {

	private final Set<Address> receivedMarkersFrom = Sets.newHashSet();
	private final Map<Address, Queue<IMessage>> messageQueues = Maps.newHashMap();
	private final Object lock = new Object();
	
	private volatile boolean localStateRecorded = false;

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(IMessage message, Address from) {
		synchronized (lock) {
			if (message instanceof Marker) {
				if (!localStateRecorded) {
					messageQueues.put(from, Queues.<IMessage>newArrayDeque());
					recordLocalState();
				}
				else {
					// record state of c as current content of Q(c).
				}
			}
			else {
				if (localStateRecorded || receivedMarkersFrom.contains(from)) {
					messageQueues.get(from).add(message);
				}
				else {
					handleMessage(message);
				}
			}
		}
	}

	private void handleMessage(IMessage message) {
		// TODO Auto-generated method stub
		
	}

	private void recordLocalState() {
		synchronized (lock) {
			recordInternalState();
			localStateRecorded = true;
			
			broadcast(new Marker());
			
			messageQueues.clear();
			for (Address address : getRemoteAddresses()) {
				messageQueues.put(address, Queues.<IMessage>newArrayDeque());
			}
		}
	}

	private void recordInternalState() {
		// TODO Auto-generated method stub
		
	}

}

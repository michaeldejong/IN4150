package nl.tudelft.ewi.in4150.group18;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import nl.tudelft.in4150.group18.network.Address;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

public class MultiQueue<T> {

	public static <T> MultiQueue<T> create() {
		return new MultiQueue<T>();
	}

	private final Map<Address, Queue<T>> queues = Maps.newConcurrentMap();
	
	public Queue<T> getQueue(Address address) {
		synchronized (queues) {
			if (queues.containsKey(address)) {
				return queues.get(address);
			}
			
			return createQueue(address);
		}
	}
	
	public Queue<T> clearQueue(Address address) {
		synchronized (queues) {
			if (queues.containsKey(address)) {
				Queue<T> queue = queues.get(address);
				queue.clear();
				return queue;
			}
			else {
				return createQueue(address);
			}
		}
	}

	public void setContents(Address address, Queue<T> queue) {
		synchronized (queues) {
			Queue<T> internalQueue = getQueue(address);
			
			internalQueue.clear();
			Iterator<T> iterator = queue.iterator();
			while (iterator.hasNext()) {
				internalQueue.add(iterator.next());
			}
		}
	}

	private Queue<T> createQueue(Address address) {
		Queue<T> queue = Queues.newArrayDeque();
		queues.put(address, queue);
		return queue;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		
		synchronized (queues) {
			for (Entry<Address, Queue<T>> entry : queues.entrySet()) {
				builder.append("[" + entry.getKey() + ", " + entry.getValue() + "]");
			}
		}
		
		builder.append("]");
		return builder.toString();
	}
	
}

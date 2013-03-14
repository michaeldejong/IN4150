package nl.tudelft.ewi.in4150.group18;

import java.util.Map;

import com.google.common.collect.Maps;

public class MarkerCounter {

	private final Map<Marker, Integer> queues = Maps.newConcurrentMap();
	
	public void add(Marker marker) {
		synchronized (queues) {
			Integer i = queues.get(marker);
			if (i == null) {
				i = 0;
			}
			
			queues.put(marker, i + 1);
		}
	}
	
	public int getCount(Marker marker) {
		synchronized (queues) {
			Integer integer = queues.get(marker);
			if (integer == null) {
				return 0;
			}
			return integer;
		}
	}
	
}

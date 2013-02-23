package nl.tudelft.in4150.group18;


public class Clock {

	private final Object lock = new Object();
	
	private volatile long internalTime = 0;
	
	public void updateWithExternalTime(long time) {
		synchronized (lock) {
			internalTime = Math.max(internalTime + 1, time);
		}
	}
	
	public long getTime() {
		synchronized (lock) {
			return internalTime;
		}
	}

	public void increment() {
		synchronized (lock) {
			internalTime += 1;
		}
	}
	
}

package nl.tudelft.in4150.group18.utils;


public class Clock {

	private final Object lock = new Object();
	
	private volatile long internalTime = 0;
	
	/**
	 * This method synchronizes the time with an external {@link Clock}'s time.
	 * 
	 * @param time	The time of the external {@link Clock}.
	 */
	public void updateWithExternalTime(long time) {
		synchronized (lock) {
			internalTime = Math.max(internalTime + 1, time);
		}
	}
	
	/**
	 * @return	The current internal time of the {@link Clock}.
	 */
	public long getTime() {
		synchronized (lock) {
			return internalTime;
		}
	}

	/**
	 * Increments the internal time of the {@link Clock} by 1.
	 */
	public void increment() {
		synchronized (lock) {
			internalTime += 1;
		}
	}
	
}

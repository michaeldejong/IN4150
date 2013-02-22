package nl.tudelft.in4150.group18;

import java.rmi.RemoteException;

import nl.tudelft.in4150.group18.TotalOrdering.Counter;
import nl.tudelft.in4150.group18.common.IRemoteObject.Message;
import nl.tudelft.in4150.group18.network.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a sample implementation of a {@link DistributedAlgorithm}.
 */
public class TotalOrdering extends DistributedAlgorithm<Counter> {

	private static final Logger log = LoggerFactory.getLogger(TotalOrdering.class);
	
	@Override
	public void start() {
		broadcast(new Counter(1), false);
	}

	@Override
	public void onMessage(Counter message, Address from) {
		try {
			Counter reply = new Counter(message.getCounter() + 1);
			send(reply, from);
			
			if (reply.getCounter() % 100 == 0) {
				log.info(getLocalAddress() + " - Processed " + reply.getCounter() + " messages.");
			}
		} 
		catch (RemoteException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * A simple {@link Message} containing a counter field.
	 */
	public static class Counter extends Message {

		private static final long serialVersionUID = 3585089413268308745L;
		
		private final int counter;
		
		public Counter(int counter) {
			this.counter = counter;
		}
		
		public int getCounter() {
			return counter;
		}
		
	}

}

package nl.tudelft.in4150.group18;

import java.rmi.RemoteException;
import java.util.Random;

import nl.tudelft.in4150.group18.common.TotalOrdering;
import nl.tudelft.in4150.group18.common.TotalOrdering_RMI;
import nl.tudelft.in4150.group18.network.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;

public class Main {
	
	private static final Range<Integer> RANGE = Range.closed(1099, 1110);
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) throws RemoteException {
		int myPort = selectRandomPort();
		
		Node<TotalOrdering_RMI> node = new Node<TotalOrdering_RMI>("localhost", myPort, true);
		node.start(new TotalOrdering(node));

		while (true) {
			for (int i = RANGE.lowerEndpoint(); i <= RANGE.upperEndpoint(); i++) {
				if (i == myPort) {
					continue;
				}
				
				try {
					TotalOrdering_RMI client = node.getClient("localhost", i);
					log.error(">>> Sending message to\tlocalhost:" + i);
					client.message(node.getLocalAddress(), "Hello world!");
				}
				catch (RemoteException e) {
					// Most likely no node running on this port.
				}
			}
		}
	}
		
	private static int selectRandomPort() {
		int offset = new Random().nextInt(RANGE.upperEndpoint() - RANGE.lowerEndpoint());
		return RANGE.lowerEndpoint() + offset;
	}

}

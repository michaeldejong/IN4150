package nl.tudelft.in4150.group18;

import java.rmi.RemoteException;
import java.util.Random;

import nl.tudelft.in4150.group18.common.SendableObject;
import nl.tudelft.in4150.group18.common.IRemoteMethods;
import nl.tudelft.in4150.group18.network.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;

public class Main {
	
	private static final Range<Integer> RANGE = Range.closed(1099, 1110);
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) throws RemoteException {
		int myPort = selectRandomPort();
		
		Node<IRemoteMethods> node = new Node<IRemoteMethods>("localhost", myPort, true);
		node.start(new SendableObject(node));

		while (true) {
			for (int i = RANGE.lowerEndpoint(); i <= RANGE.upperEndpoint(); i++) {
				if (i == myPort) {
					continue;
				}
				
				try {
					IRemoteMethods sender = node.getSender("localhost", i);
					log.info(">>> Sending message to\tlocalhost:" + i);
					
					// TODO implement random delay
					sender.message(node.getLocalAddress(), "Hello world!");
				}
				catch (RemoteException e) {
					// Most likely no node running on this port.
					// TODO after x tries delete sender or suspend sending
				}
			}
		}
	}
		
	private static int selectRandomPort() {
		int offset = new Random().nextInt(RANGE.upperEndpoint() - RANGE.lowerEndpoint());
		return RANGE.lowerEndpoint() + offset;
	}

}

package nl.tudelft.in4150.group18;

import java.io.IOException;
import java.net.InetAddress;

import nl.tudelft.in4150.group18.common.IRemoteObject.IMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AlgorithmTest {

	private NodeController<IMessage> controller1, controller2, controller3;
	private TotalOrdering algorithm1, algorithm2, algorithm3;
	private MessageConsumer consumer1, consumer2, consumer3;
	
	@Before
	public void setUp() throws IOException {
		consumer1 = new MessageConsumer();
		consumer2 = new MessageConsumer();
		consumer3 = new MessageConsumer();
		
		algorithm1 = new TotalOrdering(consumer1);
		algorithm2 = new TotalOrdering(consumer2);
		algorithm3 = new TotalOrdering(consumer3);
		
		InetAddress localHost = InetAddress.getLocalHost();
		controller1 = new NodeController<>(localHost, true, algorithm1);
		controller2 = new NodeController<>(localHost, true, algorithm2);
		controller3 = new NodeController<>(localHost, true, algorithm3);
		
		controller1.addRemote(controller2.getLocalAddress());
		controller1.addRemote(controller3.getLocalAddress());
		controller2.addRemote(controller1.getLocalAddress());
		controller2.addRemote(controller3.getLocalAddress());
		controller3.addRemote(controller1.getLocalAddress());
		controller3.addRemote(controller2.getLocalAddress());
	}
	
	@Test
	public void testSingleBroadcast() {
		algorithm1.broadcast(new Message(new MessageIdentifier(0, controller1.getLocalAddress())));
		while (consumer2.getReceivedMessages().size() < 1 || consumer3.getReceivedMessages().size() < 1) {
			// Busy wait, while we wait for both consumers to receive the message.
		}
		
		Assert.assertEquals(0, consumer2.getReceivedMessages().get(0).getTimestamp().getTimestamp());
		Assert.assertEquals(0, consumer3.getReceivedMessages().get(0).getTimestamp().getTimestamp());
	}
	
}

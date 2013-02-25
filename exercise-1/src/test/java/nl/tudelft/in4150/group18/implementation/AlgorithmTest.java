package nl.tudelft.in4150.group18.implementation;

import java.io.IOException;
import java.net.InetAddress;

import nl.tudelft.in4150.group18.NodeController;
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

		// fully connected network
		controller1.addRemote(controller2.getLocalAddress());
		controller1.addRemote(controller3.getLocalAddress());
		controller2.addRemote(controller1.getLocalAddress());
		controller2.addRemote(controller3.getLocalAddress());
		controller3.addRemote(controller1.getLocalAddress());
		controller3.addRemote(controller2.getLocalAddress());
	}

	/**
	 * Test to see if the message received has the same id as the message that was sent.
	 */
	@Test(timeout = 30000)
	public void testSingleBroadcast() {
		algorithm1.broadcast(new Message(new MessageIdentifier(0, controller1.getLocalAddress())));
		while (consumer2.numberOfMessagesDelivered() < 1 || consumer3.numberOfMessagesDelivered() < 1) {
			// Busy wait, while we wait for both consumers to receive the message.
		}

		Assert.assertEquals(0, consumer2.getReceivedMessages().get(0).getId().getTimestamp());
		Assert.assertEquals(0, consumer3.getReceivedMessages().get(0).getId().getTimestamp());
	}

	/**
	 * Test the holding of message to send
	 * @throws InterruptedException if sleep is interrupted
	 */
	@Test(timeout = 30000)
	public void testHoldingAndReleasingMessages() throws InterruptedException {
		controller1.holdMessagesToSend();
		algorithm1.broadcast(new Message(new MessageIdentifier(0, controller1.getLocalAddress())));

		Thread.sleep(2000);

		if (consumer2.numberOfMessagesDelivered() >= 1 || consumer3.numberOfMessagesDelivered() >= 1) {
			throw new IllegalStateException("Received messages while they should be held"); // controller 1 has send stuff illegally
		}

		controller1.releaseMessages();

		while (consumer2.numberOfMessagesDelivered() < 1 || consumer3.numberOfMessagesDelivered() < 1) { // throws time out exception after default 30 sec
			// Busy wait, while we wait for both consumers to receive the message.
		}

		Assert.assertEquals(0, consumer2.getReceivedMessages().get(0).getId().getTimestamp());
		Assert.assertEquals(0, consumer3.getReceivedMessages().get(0).getId().getTimestamp());
	}

	/**
	 * Test sending from all nodes to all nodes at the same time
	 */
	@Test(timeout = 30000)
	public void testMessageOrdering() {
		algorithm3.broadcast(new Message(new MessageIdentifier(1, controller3.getLocalAddress()))); // sends to 2 nodes
		algorithm2.broadcast(new Message(new MessageIdentifier(1, controller2.getLocalAddress()))); // sends to 2 nodes
		algorithm1.broadcast(new Message(new MessageIdentifier(1, controller1.getLocalAddress()))); // sends to 2 nodes

		while (consumer1.numberOfMessagesDelivered() < 2 
			|| consumer2.numberOfMessagesDelivered() < 2
			|| consumer3.numberOfMessagesDelivered() < 2) { 
			// Busy wait, while we wait for all consumers to receive all messages.
		}

		// Controller 1 received from 2 and then 3
		Assert.assertEquals(controller2.getLocalAddress(), consumer1.getReceivedMessages().get(0).getId().getAddress());
		Assert.assertEquals(controller3.getLocalAddress(), consumer1.getReceivedMessages().get(1).getId().getAddress());

		// Controller 2 received from 1 and then 3
		Assert.assertEquals(controller1.getLocalAddress(), consumer2.getReceivedMessages().get(0).getId().getAddress());
		Assert.assertEquals(controller3.getLocalAddress(), consumer2.getReceivedMessages().get(1).getId().getAddress());

		// Controller 3 received from 1 and then 2
		Assert.assertEquals(controller1.getLocalAddress(), consumer3.getReceivedMessages().get(0).getId().getAddress());
		Assert.assertEquals(controller2.getLocalAddress(), consumer3.getReceivedMessages().get(1).getId().getAddress());
	}

}

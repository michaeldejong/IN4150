package nl.tudelft.in4150.group18;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		Simulator.start(new TotalOrdering(new MessageConsumer()), args);
	}

}

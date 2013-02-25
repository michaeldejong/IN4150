package nl.tudelft.in4150.group18.implementation;

import java.io.IOException;

import nl.tudelft.in4150.group18.Simulator;

public class Main {

	public static void main(String[] args) throws IOException {
		Simulator.start(new TotalOrdering(new MessageConsumer()), args);
	}

}

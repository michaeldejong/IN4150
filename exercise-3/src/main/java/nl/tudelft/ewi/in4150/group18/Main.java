package nl.tudelft.ewi.in4150.group18;


import java.io.IOException;

import nl.tudelft.in4150.group18.Simulator;

public class Main {
	
	public static void main(String[] args) throws IOException {
		Simulator.start(new ByzantineAgreement(), args);
	}

}

package nl.tudelft.ewi.in4150.group18;

import java.io.IOException;

import nl.tudelft.in4150.group18.Simulator;

public class Main {

	public static void main(String[] args) throws IOException {
		if (Simulator.containsParam(args, "-traitor")) {
			Simulator.start(new ByzantineAgreement(true, false), args);
		} else if (Simulator.containsParam(args, "-faulty")) {
			Simulator.start(new ByzantineAgreement(false, true), args);
		} else {
			Simulator.start(new ByzantineAgreement(false, false), args);
		}
	}
}

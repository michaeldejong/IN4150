package nl.tudelft.in4150.group18;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		new Simulator() {
			@Override
			DistributedAlgorithm<?> getAlgorithm() {
				return new TotalOrdering();
			}
		}.start(args);
	}

}

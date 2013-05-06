package nl.tudelft.ewi.in4150.group18;


import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.Simulator;

public class Main {
	
	private static final int GENERALS = 10;
	
	private static ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(GENERALS);
	
	public static void main(final String[] args) throws IOException {
		for (int i = 0; i < GENERALS; i++) {
			final int j = i;
			executor.submit(new Runnable() {
				@Override
				public void run() {
					ByzantineAgreement algorithm = new ByzantineAgreement(Type.ATTACK);
					algorithm.setMaximumFaults(2);
					
					try {
						if (j == 0) {
							Simulator.start(algorithm, new String[] { "--local", "--ui" });
						}
						else {
							Simulator.start(algorithm, new String[] { "--local" });
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

}

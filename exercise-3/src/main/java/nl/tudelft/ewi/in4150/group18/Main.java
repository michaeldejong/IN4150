package nl.tudelft.ewi.in4150.group18;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.Simulator;

public class Main {

	private static final int GENERALS = 4;
	private static final int FAULTY = 1;
	private static final int TRAITORS = 0;
	
	private static final ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(GENERALS);
	
	public static void main(String[] args) throws IOException {
		startThreaded(new String[] { "--ui", "--local", "--default:attack" });
		
		int faulty = FAULTY;
		int traitors = TRAITORS;
		for (int i = 1; i < GENERALS; i++) {
			if (faulty > 0) {
				faulty--;
				startThreaded(new String[] { "--local", "--default:retreat", "--faulty" });
			}
			else if (traitors > 0) {
				traitors--;
				startThreaded(new String[] { "--local", "--default:retreat", "--traitor" });
			}
			else {
				startThreaded(new String[] { "--local", "--default:retreat" });
			}
		}
	}

	private static void start(String[] args) throws IOException {
		Type command = Simulator.containsParam(args, "--default:attack") ? Type.ATTACK : Type.RETREAT;
		
		if (Simulator.containsParam(args, "--traitor")) {
			Simulator.start(new ByzantineAgreement(command, true, false), args);
		} else if (Simulator.containsParam(args, "--faulty")) {
			Simulator.start(new ByzantineAgreement(command, false, true), args);
		} else {
			Simulator.start(new ByzantineAgreement(command, false, false), args);
		}
	}
	
	private static void startThreaded(final String[] args) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					start(args);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}

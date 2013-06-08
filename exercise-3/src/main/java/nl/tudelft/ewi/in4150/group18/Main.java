package nl.tudelft.ewi.in4150.group18;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import nl.tudelft.ewi.in4150.group18.Command.Type;
import nl.tudelft.in4150.group18.Simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	private static final int GENERALS = 4;
	private static final int FAULTY = 0;
	private static final int TRAITORS = 1;
	private static final int MAX_FAULTS = 1;
	
	private static final ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(GENERALS);
	
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			run(GENERALS, FAULTY, TRAITORS, MAX_FAULTS);
		}
		else {
			start(args, MAX_FAULTS);
		}
	}
	
	public static void run(int generals, int faulty, int traitors, int maxFaults) throws IOException {
		if (generals < 3 * (faulty + traitors)) {
			log.warn("Too many traitors or faulty processes to come to concensus!");
		}
		
		startThreaded(new String[] { "--ui", "--local", "--default:attack" }, maxFaults);
		
		for (int i = 1; i < GENERALS; i++) {
			if (faulty > 0) {
				faulty--;
				startThreaded(new String[] { "--local", "--default:retreat", "--faulty" }, maxFaults);
			}
			else if (traitors > 0) {
				traitors--;
				startThreaded(new String[] { "--local", "--default:retreat", "--traitor" }, maxFaults);
			}
			else {
				startThreaded(new String[] { "--local", "--default:retreat" }, maxFaults);
			}
		}
	}

	private static void start(String[] args, int maxFaults) throws IOException {
		Type command = Simulator.containsParam(args, "--default:attack") ? Type.ATTACK : Type.RETREAT;
		
		Lieutenant lieutenant;
		if (Simulator.containsParam(args, "--traitor")) {
			lieutenant = new Traitor(command);
		} else if (Simulator.containsParam(args, "--faulty")) {
			lieutenant = new Faulty(command);
		} else {
			lieutenant = new Lieutenant(command);
		}
		
		lieutenant.setMaximumFaults(maxFaults);
		Simulator.start(lieutenant, args);
	}
	
	private static void startThreaded(final String[] args, final int maxFaults) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					start(args, maxFaults);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}

package nl.tudelft.in4150.group18;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import nl.tudelft.in4150.group18.ui.MainUI;
import nl.tudelft.in4150.group18.ui.NetworkInterfaceChooserDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simulator {
	
	private static final Logger log = LoggerFactory.getLogger(Simulator.class);
	
	private Simulator() {
		// Prevent instantiation.
	}

	public static void start(DistributedAlgorithm algorithm, String[] args) throws IOException {
		boolean isLocal = containsParam(args, "--local");
		boolean missingAdditionalParams = !isLocal && !containsParam(args, "--interface");
		
		if (containsParam(args, "--ui")) { 
			if (missingAdditionalParams) {
				promptConfigDialog(algorithm);
			}
			else {
				InetAddress localAddress = getHostAddress(isLocal, args);
				new MainUI(isLocal, localAddress, algorithm);
			}
		}
		else {
			if (missingAdditionalParams) {
				System.out.println("At least one of the following parameters is required:");
				System.out.println("  --ui                Opens a graphic user interface.");
				System.out.println("  --local             Indicates that this algorithm only runs amongst local nodes.");
				System.out.println("  --interface name    Indicates that this algorithm runs across the network, using a specific network interface.");
				System.out.println();
				System.exit(0);
			}
			
			InetAddress localAddress = getHostAddress(isLocal, args);
			new NodeController(localAddress, isLocal, algorithm);
		}
	}

	/**
	 * This method opens a dialog prompting the user for network information to be able to run the code. 
	 * 
	 * @param algorithm			The {@link DistributedAlgorithm} to run.
	 * @throws SocketException	In case we couldn't retrieve all the required network information to
	 * 							form the cluster.
	 */
	private static void promptConfigDialog(final DistributedAlgorithm algorithm) throws SocketException {
		new NetworkInterfaceChooserDialog(new NetworkInterfaceChooserDialog.Callback() {
			@Override
			public void onSelect(boolean local, String networkInterface) {
				try {
					InetAddress hostAddress = InetAddress.getLocalHost();
					if (networkInterface != null) {
						hostAddress = getHostAddressFromInterface(networkInterface);
					}
					new MainUI(local, hostAddress, algorithm);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					System.exit(1);
				}
			}
		});
	}

	/**
	 * This method determines the {@link InetAddress} to use in a specific mode of operation.
	 * 
	 * @param isLocal		True if the cluster should run on a single machine.
	 * @param params		The array of parameters specific to the main method.
	 * @return				The {@link InetAddress} to use for communication.
	 * @throws IOException	If we couldn't retrieve network information.
	 */
	private static InetAddress getHostAddress(boolean isLocal, String[] params) throws IOException {
		if (isLocal) {
			return InetAddress.getLocalHost();
		}
		
		String interfaceName = params[findParam(params, "--interface") + 1];
		return getHostAddressFromInterface(interfaceName);
	}

	/**
	 * This method determines the {@link InetAddress} of the local machine for a specific {@link NetworkInterface}.
	 * 
	 * @param interfaceName		The name of the {@link NetworkInterface}.
	 * @return					The local {@link InetAddress} of the specified {@link NetworkInterface}. 
	 * @throws SocketException	If there were problems with retrieving information from the {@link NetworkInterface}.
	 */
	private static InetAddress getHostAddressFromInterface(String interfaceName) throws SocketException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			if (networkInterface.getName().equalsIgnoreCase(interfaceName)) {
				Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress address = inetAddresses.nextElement();
					if (address instanceof Inet4Address) {
						return address;
					}
				}
				
				throw new IllegalArgumentException("Could not locate IPv4 address for interface: " + interfaceName);
			}
		}
		
		throw new IllegalArgumentException("Could not locate network interface: " + interfaceName);
	}

	/**
	 * THis method checks the array of parameters for a specific needle.
	 * 
	 * @param params	The array of parameters.
	 * @param needle	The needle to look for in the array of parameters.
	 * @return			True if the needle occurs in the array or false otherwise.
	 */
	private static boolean containsParam(String[] params, String needle) {
		return findParam(params, needle) >= 0;
	}
	
	/**
	 * This method returns the index of the needle in the parameters.
	 * 
	 * @param params	The array of parameters
	 * @param needle	The parameter to look for.
	 * @return			The index of the needle in the array of parameters. This method 
	 * 					will return -1 if the needle does not occur in the parameters.
	 */
	private static int findParam(String[] params, String needle) {
		for (int i = 0; i < params.length; i++) {
			if (params[i].equalsIgnoreCase(needle)) {
				return i;
			}
		}
		return -1;
	}
	
}

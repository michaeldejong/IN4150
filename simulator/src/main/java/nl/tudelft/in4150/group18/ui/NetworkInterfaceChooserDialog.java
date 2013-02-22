package nl.tudelft.in4150.group18.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class NetworkInterfaceChooserDialog extends JDialog {
	
	private final Callback callback;

	public NetworkInterfaceChooserDialog(Callback callback) throws SocketException {
		this.callback = callback;
		
		setSize(400, 240);
		setLocation(200, 200);
		setTitle("Select modus operandi");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setModal(true);
		
		renderUI();
		setVisible(true);
		toFront();
	}
	
	private void renderUI() throws SocketException {
		final JRadioButton local = new JRadioButton("Run locally on single machine");
		final JRadioButton network = new JRadioButton("Run across multiple machines");
		
		JPanel localityPanel = renderLocalityBox(local, network);
		
		Vector<String> interfaces = listInterfaces();
		final JComboBox<String> networkInterfaceBox = new JComboBox<>(interfaces);
		
		JPanel networkInterfaceGroup = renderNetworkInterfaceBox(networkInterfaceBox);
		
		final JButton confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				callback.onSelect(local.isSelected(), local.isSelected() ? null : (String) networkInterfaceBox.getSelectedItem());
				dispose();
			}
		});
		
		networkInterfaceBox.setEnabled(false);
		
		if (interfaces.isEmpty()) {
			network.setEnabled(false);
		}
		
		local.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (local.isSelected()) {
					networkInterfaceBox.setEnabled(false);
				}
			}
		});
		
		network.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (network.isSelected()) {
					networkInterfaceBox.setEnabled(true);
				}
			}
		});
		
		GroupLayout layout = new GroupLayout(getContentPane());
		setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGap(5)
				.addGroup(layout.createParallelGroup(Alignment.TRAILING)
						.addComponent(localityPanel)
						.addComponent(networkInterfaceGroup)
						.addComponent(confirmButton)
				)
				.addGap(5));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGap(5)
				.addComponent(localityPanel)
				.addGap(5)
				.addComponent(networkInterfaceGroup)
				.addGap(5)
				.addComponent(confirmButton)
				.addGap(5, 5, Integer.MAX_VALUE));
		
	}
	
	private JPanel renderNetworkInterfaceBox(JComboBox<String> comboBox) {
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Select the appropriate network interface"));
		panel.setMinimumSize(new Dimension(1, 1));
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGap(5)
				.addComponent(comboBox)
				.addGap(5));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGap(5)
				.addComponent(comboBox)
				.addGap(5));
		
		return panel;
	}

	private JPanel renderLocalityBox(JRadioButton local, JRadioButton network) {
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Select modus operandi"));
		panel.setMinimumSize(new Dimension(1, 1));
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGap(5)
				.addGroup(layout.createParallelGroup()
						.addComponent(local)
						.addComponent(network)
				)
				.addGap(5));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGap(5)
				.addComponent(local)
				.addGap(2)
				.addComponent(network)
				.addGap(5));
		
		ButtonGroup group = new ButtonGroup();
		group.add(local);
		group.add(network);
		local.setSelected(true);
		
		return panel;
	}

	private Vector<String> listInterfaces() throws SocketException {
		Vector<String> results = new Vector<>();
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			if (containsIPv4Address(networkInterface.getInetAddresses())) {
				results.add(networkInterface.getName());
			}
		}
		return results;
	}
	
	private boolean containsIPv4Address(Enumeration<InetAddress> addresses) {
		while (addresses.hasMoreElements()) {
			InetAddress address = addresses.nextElement();
			if (address instanceof Inet4Address) {
				return true;
			}
		}
		return false;
	}
	
	public interface Callback {
		void onSelect(boolean local, String networkInterface);
	}

}

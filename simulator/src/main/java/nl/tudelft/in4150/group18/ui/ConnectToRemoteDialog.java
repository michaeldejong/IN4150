package nl.tudelft.in4150.group18.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

import nl.tudelft.in4150.group18.network.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ConnectToRemoteDialog extends JDialog {
	
	private static final Logger log = LoggerFactory.getLogger(ConnectToRemoteDialog.class);
	
	private final Callback callback;

	public ConnectToRemoteDialog(Callback callback) throws SocketException {
		this.callback = callback;
		
		setSize(400, 70);
		setLocation(200, 200);
		setTitle("Connect to remote");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setModal(true);

		renderUI();
		setVisible(true);
		toFront();
	}
	
	private void renderUI() throws SocketException {
		final JTextField addressField = new JTextField("hostname / IP address");
		final JTextField portField = new JTextField("port");
		
		final JButton addButton = new JButton("Connect");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String text = addressField.getText();
					InetAddress address = InetAddress.getByName(text);
					callback.addNode(new Address(address.getHostAddress(), Integer.parseInt(portField.getText())));
				} 
				catch (UnknownHostException e1) {
					log.warn(e1.getMessage(), e1);
				}
				finally {
					dispose();
				}
			}
		});
		
		GroupLayout layout = new GroupLayout(getContentPane());
		setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGap(10)
				.addComponent(addressField)
				.addGap(5)
				.addComponent(portField)
				.addGap(5)
				.addComponent(addButton)
				.addGap(10));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGap(10)
				.addGroup(layout.createParallelGroup()
						.addComponent(addressField)
						.addComponent(portField)
						.addComponent(addButton)
				)
				.addGap(10, Integer.MAX_VALUE, Integer.MAX_VALUE));
		
	}

	interface Callback {
		void addNode(Address address);
	}

}

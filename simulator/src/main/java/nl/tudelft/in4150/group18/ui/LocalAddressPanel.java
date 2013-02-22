package nl.tudelft.in4150.group18.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import nl.tudelft.in4150.group18.network.Address;

@SuppressWarnings("serial")
public class LocalAddressPanel extends JPanel {

	private JLabel myAddress;

	public LocalAddressPanel() {
		setBorder(new TitledBorder("Local address:"));
		setMinimumSize(new Dimension(0, 54));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
		
		renderUI();
	}
	
	private void renderUI() {
		myAddress = new JLabel("");
		myAddress.setHorizontalTextPosition(JLabel.LEFT);
		myAddress.setForeground(Color.GRAY);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGap(12)
				.addComponent(myAddress));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGap(5)
				.addComponent(myAddress));
	}

	public void setLocalAddress(Address address) {
		myAddress.setText(address.getHostAddress() + ":" + address.getPort());
	}

}

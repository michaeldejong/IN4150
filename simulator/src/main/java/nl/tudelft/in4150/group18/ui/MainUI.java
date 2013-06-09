package nl.tudelft.in4150.group18.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.security.AccessControlException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import nl.tudelft.in4150.group18.NodeController;
import nl.tudelft.in4150.group18.SynchronousDistributedAlgorithm;
import nl.tudelft.in4150.group18.network.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class MainUI<R> extends JFrame {
	
	private static final Logger log = LoggerFactory.getLogger(MainUI.class);
	
	private final Timer timer;
	private final LocalAddressPanel myAddressPanel;
	private final NodeController<R> main;
	private final JTextArea remotesPanel;
	
	public MainUI(boolean localOnly, InetAddress localAddress, SynchronousDistributedAlgorithm<R> algorithm) throws IOException {
		setTitle("IN4150: Distributed algorithms - Group 18");
		setSize(570, 400);
		setLocation(100, 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.timer = new Timer(true);
		this.main = new NodeController<R>(localAddress, localOnly, algorithm);
		this.myAddressPanel = new LocalAddressPanel();
		this.myAddressPanel.setLocalAddress(main.getLocalAddress());
		this.remotesPanel = new JTextArea();

		renderUI();
		setVisible(true);
		
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						listRemoteNodes();
					}
				});
			}
		}, 1000, 1000);
	}
	
	private void renderUI() {
		remotesPanel.setEditable(false);
		remotesPanel.setBackground(getBackground());
		remotesPanel.setLayout(new BoxLayout(remotesPanel, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getViewport().add(remotesPanel);
		scrollPane.setMinimumSize(new Dimension(0, 0));
		scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		scrollPane.setBorder(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Remote addresses:"));

		GroupLayout layout1 = new GroupLayout(panel);
		panel.setLayout(layout1);
		
		layout1.setHorizontalGroup(layout1.createSequentialGroup()
				.addGap(10)
				.addComponent(scrollPane)
				.addGap(10));
		
		layout1.setVerticalGroup(layout1.createSequentialGroup()
				.addGap(10)
				.addComponent(scrollPane)
				.addGap(10));
		
		final JButton autoDetect = new JButton("Auto-detect remotes");
		autoDetect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				main.autoDetectRemotes();
			}
		});
		
		final JButton add = new JButton("Connect to node");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showAddRemoteDialog();
			}
		});
		
	    final JButton graph = new JButton("Show graph");
	    graph.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	    	GraphDialog.getInstance().setLocation(
	            (int) MainUI.this.getLocation().getX() + MainUI.this.getWidth(),
	            (int) MainUI.this.getLocation().getY());
	        GraphDialog.getInstance().setVisible(true);
	        GraphDialog.getInstance().position();
	      }
	    });
		
		final JButton start = new JButton("Start algorithm");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				autoDetect.setEnabled(false);
				add.setEnabled(false);
				main.start();
			}
		});
		
		GroupLayout layout = new GroupLayout(getContentPane());
		setLayout(layout);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGap(10)
				.addGroup(layout.createParallelGroup()
						.addComponent(myAddressPanel)
						.addComponent(panel)
						.addGroup(layout.createSequentialGroup()
								.addComponent(autoDetect)
								.addGap(10)
								.addComponent(add)
								.addGap(10)
								.addComponent(graph)
								.addGap(10)
								.addComponent(start)
						)
				)
				.addGap(10));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGap(10)
				.addComponent(myAddressPanel)
				.addGap(10)
				.addComponent(panel)
				.addGap(10)
				.addGroup(layout.createParallelGroup()
						.addComponent(autoDetect)
						.addComponent(add)
						.addComponent(graph)
						.addComponent(start)
				)
				.addGap(10));
	}
	
	private void showAddRemoteDialog() {
		try {
			new ConnectToRemoteDialog(new ConnectToRemoteDialog.Callback() {
				@Override
				public void addNode(Address address) {
					try {
						main.addRemote(address);
					}
					catch (RemoteException e) {
						log.error("Could not add remote: {}", address);
					}
					catch (AccessControlException e) {
						log.warn("You cannot add remotes which are running on the same machine, when running in cluster mode!", e);
					}
				}
			});
		} catch (SocketException e1) {
			log.error(e1.getMessage(), e1);
		}
	}

	private void listRemoteNodes() {
		StringBuilder builder = new StringBuilder();
		for (Address address : main.listRemoteAddresses()) {
			builder.append(" - " + address.getHostAddress() + ":" + address.getPort() + "\n");
		}
		remotesPanel.setText(builder.toString());
	}

}

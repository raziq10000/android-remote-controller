
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.State;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.*;
import javax.swing.*;


public class ServerScreen{
	private JFrame frame;
	private JTextArea textArea;
	private UdpServer udpServer;
	private TcpServer tcpServer;
	private BluetoothServer bluetoothServer;
	private JPanel panel;
	private JButton btnNewButton;
	private JRadioButton connectionTypeRbttn;
	private JButton btnStart;
	private JRadioButton wifiConnectionRdbtn;
	private JRadioButton bluetoothRdbtn;
	
	
	public static Logger LOGGER = Logger.getLogger(ServerScreen.class.getName());
	
	
	public static void main(String[] args) {
			
	    try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 	
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ServerScreen window = new ServerScreen();
					
		window.frame.setVisible(true);
		
		//Server server = window.server;	
		
	}

	/**
	 * Create the application.
	 */
	public ServerScreen() {
		LOGGER.addHandler(new TextAreaHandler());
		initialize();
		
		
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {			
			
			@Override
			public void windowClosed(WindowEvent e) {
				tcpServer.interrupt();	
				udpServer.interrupt();
			}			
			
		});
		
		frame.setBounds(100, 100, 400, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
		);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Log", null, scrollPane, null);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		panel = new JPanel();
		tabbedPane.addTab("New tab", null, panel, null);
		
		btnNewButton = new JButton("Stop");
		btnNewButton.setEnabled(false);
	
		btnNewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(wifiConnectionRdbtn.isSelected()) {
					tcpServer.interrupt();
					udpServer.interrupt();	
				}
				if(bluetoothRdbtn.isSelected()) {
					bluetoothServer.interrupt();
				}
				
				btnNewButton.setEnabled(false);
				btnStart.setEnabled(true);
				
			}
		});
		
		btnStart = new JButton("Start");
	
		
		btnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(wifiConnectionRdbtn.isSelected())
				{
					if(tcpServer == null || tcpServer.getState() != State.NEW){
						tcpServer = new TcpServer();
					}
					if(udpServer == null || udpServer.getState() != State.NEW){
						udpServer = new UdpServer();
					}
					tcpServer.start();
					udpServer.start();
					
				}
				
				if(bluetoothRdbtn.isSelected()) 
				{
					if(bluetoothServer == null || bluetoothServer.getState() != State.NEW){
						bluetoothServer = new BluetoothServer();
					}
					bluetoothServer.start();
				}
				
				btnNewButton.setEnabled(true);
				btnStart.setEnabled(false);
				}
				
			
		});
		
		
		
		wifiConnectionRdbtn = new JRadioButton("Wifi");
		
		bluetoothRdbtn = new JRadioButton("Bluetooth");
		
		JLabel lblConnectionWay = new JLabel("Connection Type");
		
		wifiConnectionRdbtn.setActionCommand("wifi");
		bluetoothRdbtn.setActionCommand("bluetooth");
		
		
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			     wifiConnectionRdbtn.setSelected(e.getActionCommand().equals("wifi"));
			     bluetoothRdbtn.setSelected(e.getActionCommand().equals("bluetooth"));
			}
		};
		
		wifiConnectionRdbtn.addActionListener(listener);
		bluetoothRdbtn.addActionListener(listener);
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
								.addComponent(wifiConnectionRdbtn)
								.addComponent(btnStart))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
								.addComponent(bluetoothRdbtn)))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(36)
							.addComponent(lblConnectionWay)))
					.addContainerGap(235, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(27)
					.addComponent(lblConnectionWay)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(wifiConnectionRdbtn)
						.addComponent(bluetoothRdbtn))
					.addGap(18)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnStart)
						.addComponent(btnNewButton))
					.addContainerGap(322, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		frame.getContentPane().setLayout(groupLayout);
	}
	
	private class TextAreaHandler extends java.util.logging.Handler {

	    

	    @Override
	    public void publish(final LogRecord record) {
	        SwingUtilities.invokeLater(new Runnable() {

	            @Override
	            public void run() {
	               
	                Date date = new Date(record.getMillis());
	                textArea.append("[" + date.toString() + "]" + "  " + record.getMessage() + "\n");
	            }

	        });
	    }

	    public JTextArea getTextArea() {
	        return textArea;
	    }

		@Override
		public void flush() {
			
			
		}

		@Override
		public void close() throws SecurityException {
			
			
		}

	    //...
	}
}
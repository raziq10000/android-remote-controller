import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.Thread.State;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ServerScreen {
	private JFrame frame;
	private JTextArea textArea;
	private UdpServer udpServer;
	private TcpServer tcpServer;
	private BluetoothServer bluetoothServer;
	private JPanel panel;
	private JButton btnStop;
	private JRadioButton connectionTypeRbttn;
	private JButton btnStart;
	private JRadioButton wifiConnectionRdbtn;
	private JRadioButton bluetoothRdbtn;
	private final boolean isBluetoothSupported = BluetoothServer.isBluetoothSupported();

	public static Logger LOGGER = Logger
			.getLogger(ServerScreen.class.getName());

	public static void main(String[] args) {

		/*try {
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
		}*/
		ServerScreen serverScreen = new ServerScreen();
		serverScreen.frame.setVisible(true);
		
		if(!serverScreen.isBluetoothSupported) {
			LOGGER.warning("Bluetooth device not present!");
		}
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
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(
				Alignment.LEADING).addComponent(tabbedPane,
				GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(
				Alignment.LEADING).addComponent(tabbedPane, Alignment.TRAILING,
				GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE));

		panel = new JPanel();
		tabbedPane.addTab("Connection", null, panel, null);

		btnStop = new JButton("Stop");
		btnStop.setEnabled(false);

		btnStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (wifiConnectionRdbtn.isSelected()) {
					tcpServer.interrupt();
					udpServer.interrupt();
				} else if (bluetoothRdbtn.isSelected()) {
					bluetoothServer.interrupt();
				} else {
					return;
				}

				btnStop.setEnabled(false);
				btnStart.setEnabled(true);
				wifiConnectionRdbtn.setEnabled(true);
				if(isBluetoothSupported){
					bluetoothRdbtn.setEnabled(true);
				}

			}
		});

		btnStart = new JButton("Start");

		btnStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (wifiConnectionRdbtn.isSelected()) {
					if (tcpServer == null || tcpServer.getState() != State.NEW) {
						tcpServer = new TcpServer();
					}
					if (udpServer == null || udpServer.getState() != State.NEW) {
						udpServer = new UdpServer();
					}
					tcpServer.start();
					udpServer.start();

				} else if (bluetoothRdbtn.isSelected()) {
					if (bluetoothServer == null
							|| bluetoothServer.getState() != State.NEW) {
						bluetoothServer = new BluetoothServer();
					}
					bluetoothServer.start();
				} else {
					return;
				}

				btnStop.setEnabled(true);
				btnStart.setEnabled(false);
				wifiConnectionRdbtn.setEnabled(false);
				bluetoothRdbtn.setEnabled(false);
			}

		});

		wifiConnectionRdbtn = new JRadioButton("Wifi");

		bluetoothRdbtn = new JRadioButton("Bluetooth");

		JLabel lblConnectionWay = new JLabel("Connection Type");

		wifiConnectionRdbtn.setActionCommand("wifi");
		bluetoothRdbtn.setActionCommand("bluetooth");

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(6)
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
								.addComponent(wifiConnectionRdbtn)
								.addComponent(btnStart))
							.addGap(6)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnStop, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
								.addComponent(bluetoothRdbtn)))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(36)
							.addComponent(lblConnectionWay)))
					.addGap(218))
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
					.addGap(2)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnStart)
						.addComponent(btnStop))
					.addContainerGap(369, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);

		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Log", null, scrollPane, null);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				wifiConnectionRdbtn.setSelected(e.getActionCommand().equals(
						"wifi"));
				bluetoothRdbtn.setSelected(e.getActionCommand().equals(
						"bluetooth"));
			}
		};

		wifiConnectionRdbtn.addActionListener(listener);
		bluetoothRdbtn.addActionListener(listener);
		if(!isBluetoothSupported){
			bluetoothRdbtn.setEnabled(false);
		}
		frame.getContentPane().setLayout(groupLayout);
	}

	//Handler for logging messages to text area
	private class TextAreaHandler extends java.util.logging.Handler {
		private DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		@Override
		public void publish(final LogRecord record) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					Calendar calendar = Calendar.getInstance();
					textArea.append("[" + df.format(calendar.getTime()) + "]" + " [" + record.getLevel() + "] " + " "
							+ record.getMessage() + "\n");
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

		// ...
	}
}
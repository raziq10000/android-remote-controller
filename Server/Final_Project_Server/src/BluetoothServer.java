
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;


public class BluetoothServer extends Thread {

	InputStream input;
	OutputStream output;
	StreamConnectionNotifier notifier;
	StreamConnection connection = null;
	// retrieve the local Bluetooth device object
	static LocalDevice local = null;
	boolean connected = false;
	
	public BluetoothServer() {
		if(local== null)
			try {
				local = LocalDevice.getLocalDevice();
				local.setDiscoverable(DiscoveryAgent.GIAC);
			} catch (BluetoothStateException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void run() {
		waitForConnection();
	}

	/** Waiting for connection from devices */
	private void waitForConnection() {
		// setup the server to listen for connection
		try {
			UUID uuid = new UUID("0000200000001000800000805F9B34FB", false);
			String url = "btspp://localhost:" + uuid.toString()
					+ ";name=RemoteBluetooth";
			notifier = (StreamConnectionNotifier) Connector.open(url);
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
	
	// waiting for connection
		while (true) {
			try {
				ServerScreen.LOGGER.info("Bluetooth Server waiting for connection");
				connection = notifier.acceptAndOpen();
				ServerScreen.LOGGER.info("Bluetooth connected");
				connected = true;
				input = connection.openInputStream();
				output = connection.openDataOutputStream();
				System.out.println("Connected ...");
			} catch (IOException ex) {
				ex.printStackTrace();
				connected = false;
				return;
			}
			try {
				byte b[] = new byte[1024];
				String s;
				do {
					for (int i = 0; i < b.length; i++) b[i] = 0;
	 				input.read(b);
					s = new String(b);
					s = s.trim();
					MessageHandler.getInstance().handle(s);
				} while (!s.equals("exit"));
				connection.close();
				connected = false;
			} catch (Exception e) {
				e.printStackTrace();
				connected = false;
				return;
			}
		}
	}
	
	public void interrupt() {
		try {
			if(connection != null) {
				if(connected) {
					output.write("exit".getBytes());
					output.flush();
					output.close();
					input.close();
				}
				connection.close();
			}
			if(notifier != null) 
				notifier.close();
			connected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			 super.interrupt();
			} catch(Exception e) {}
		
		ServerScreen.LOGGER.info("Server stopped...");
	}
}


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	
	public BluetoothServer() {
	}

	@Override
	public void run() {
		waitForConnection();
	}

	/** Waiting for connection from devices */
	private void waitForConnection() {
		// retrieve the local Bluetooth device object
		LocalDevice local = null;



		// setup the server to listen for connection
		try {
			local = LocalDevice.getLocalDevice();
			local.setDiscoverable(DiscoveryAgent.GIAC);

			UUID uuid = new UUID("1101", true);
			String url = "btspp://localhost:" + uuid.toString()
					+ ";name=RemoteBluetooth";
			notifier = (StreamConnectionNotifier) Connector.open(url);
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		ServerScreen.LOGGER.info("Bluetooth Server waiting for connection");
	try{	
		connection = notifier.acceptAndOpen();
		ServerScreen.LOGGER.info("Bluetooth connected");
		input = connection.openInputStream();
		output = connection.openDataOutputStream();
		System.out.println("Connected ...");
	}
	catch(IOException ex){
		ex.printStackTrace();
	}
	
	// waiting for connection
		while (true) {
			try {
				
				byte b[] = new byte[1024];
				String s;
				do {

					input.read(b);
					s = new String(b);
					s = s.trim();
					MessageHandler.getInstance().handle(s);
//                    output.write(s.getBytes());
//					System.out.println(s);

				} while (s.equals("exit"));

			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	public void interrupt() {
		super.interrupt();
		try {
			   notifier.close();
			if(connection != null)
				connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ServerScreen.LOGGER.info("Server stopped...");
	}
}

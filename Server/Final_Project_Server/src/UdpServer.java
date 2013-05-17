import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpServer extends Thread {

	private DatagramSocket socket;
	private DatagramPacket packet;
	private String hostname = "";
	private boolean isRunning = false;
	private final String CONNECTION_START_CODE = "/ARC/";
	private final String CONNECTION_CORRECTION_CODE = "ARC/";

	@Override
	public void run() {
		isRunning = true;
		
		try {
			socket = new DatagramSocket(7880);
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		while (isRunning) {
			byte[] buf = new byte[512];
			packet = new DatagramPacket(buf, buf.length);
			try {
				this.socket.receive(packet);
			} catch (IOException e) {
				isRunning = false;
				break;

			}

			String msg = new String(packet.getData());
			msg = msg.trim();

			if (msg.equals("close")) {
				socket.close();
			} else if (msg.equals(CONNECTION_START_CODE)) {

				try {
					DatagramPacket dp = new DatagramPacket(
							(CONNECTION_CORRECTION_CODE + hostname).getBytes(),
							(CONNECTION_CORRECTION_CODE + hostname).length(),
							packet.getSocketAddress());
					for (int i = 0; i < 3; i++)
						socket.send(dp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				MessageHandler.getInstance().handle(msg);
		}

	}

	@Override
	public void interrupt() {
		super.interrupt();
		isRunning = false;
		socket.close();
	}

}

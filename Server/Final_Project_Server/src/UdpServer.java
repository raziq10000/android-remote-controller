import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;


public class UdpServer extends Thread{
	
	public DatagramSocket socket;
	private DatagramPacket packet;
	private boolean isRunning = false;
	final String connection_startcode = "a";
	final String connection_correction = "f";

	@Override
	public void run() {
		isRunning = true;
		try {
			
			
			socket = new DatagramSocket(7880);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			System.out.println("55");
		}
		while(isRunning){
			byte[] buf = new byte[512];
			packet = new DatagramPacket(buf, buf.length);
			try {
				this.socket.receive(packet);
			} catch (IOException e) {
				isRunning = false;
				break
				;
				
			}
			
			String msg = new String(packet.getData());
			msg = msg.trim();
			
			if(msg.equals("close")){
				socket.close();
			}else if(msg.equals(connection_startcode)){
				
				try {
					DatagramPacket dp = new DatagramPacket(connection_correction.getBytes(), connection_correction.length(), packet.getSocketAddress());
					for (int i = 0; i < 3; i++) 
						socket.send(dp);	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else
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

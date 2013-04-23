import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;


public class RemoteFileManager {
	final static int connectionPort = 9898;
	private static Socket socket;
	static boolean isConnected = false;
	static ObjectInputStream oInputStream;
	public static void connect(String server) throws Exception {
		InetAddress ia = InetAddress.getByName(server);
		socket = new Socket(ia, connectionPort);
		isConnected = true;
		oInputStream = new ObjectInputStream(socket.getInputStream());
	}
	
	public void close() {
		try {
			if(socket != null)
				socket.close();
			isConnected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	}

	public static ArrayList<RemoteFile> getSubFiles(String absolutePath) {

		try {
			socket.getOutputStream().write(
					("subFiles/" + absolutePath + "/").getBytes());
			ArrayList<RemoteFile> readObject = (ArrayList<RemoteFile>) oInputStream.readObject();
			return readObject;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}

	}
}

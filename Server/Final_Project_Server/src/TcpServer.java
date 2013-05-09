import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TcpServer extends Thread {
	private ServerSocket server;
	private int PORT = 7878;
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;

	public ServerSocket getServerSocket() {
		return server;
	}

	public Socket getClient() {
		return client;
	}

	//Start server
	@Override
	public void run() {
		String msg = null;
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ServerScreen.LOGGER.info("Server started...");

		while (true) {
			try {
				ServerScreen.LOGGER.info("Waiting for client...");
				client = server.accept();
				ServerScreen.LOGGER.info("Client connected "+ client.getInetAddress().getHostAddress());
				in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
				out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
				MessageHandler.getInstance().setInput(client.getInputStream());
				MessageHandler.getInstance().setOutput(client.getOutputStream());

				//Wait for client messages 
				while (true) {
					msg = getFromClient();
					MessageHandler.getInstance().handle(msg);
				}

			}

			catch (Exception e) {
				//When client disconnected null comes
				if (e instanceof NullPointerException) {
					try {
						client.close();
						ServerScreen.LOGGER.info("Client disconnected "
								+ client.getInetAddress().getHostAddress());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} else if (e instanceof SocketException) {
					break;
				}

			}
		}
	}

	//Read messages from client
	public String getFromClient() throws IOException {
		String input = "";
		input = in.readLine();
		return input;

	}
	
	//Send messages to client
	public void sendToClient(String msg) {
		this.out.println(msg);
		this.out.flush();
	}

	//Stop server 
	@Override
	public void interrupt() {
		super.interrupt();
		try {
			server.close();
			if (client != null)
				client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ServerScreen.LOGGER.info("Server stopped...");
	}

}

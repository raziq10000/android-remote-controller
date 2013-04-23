import java.awt.Robot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;



public class TcpServer extends Thread{
	public ServerSocket server;
	private final int PORT = 7878;
	private Socket client;
	private Robot robot;
	private BufferedReader in;
	private PrintWriter out;
	
	
	public ServerSocket getServerSocket() {
		return server;
	}

	public Socket getClient() {
		return client;
	}
	
	@Override
	public void run() {
		String msg = null;
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ServerScreen.LOGGER.info("Server started...");
			
		while(true){
			try {
			
		
				client = server.accept();
				ServerScreen.LOGGER.info("Client connected " + client.getInetAddress().getHostAddress());
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
				MessageHandler.getInstance().setInput(client.getInputStream());
				MessageHandler.getInstance().setOutput(client.getOutputStream());
				//sendToClient("I'm Server -> " + server.getLocalSocketAddress());
				
				while(true){
					msg = getFromClient();
					MessageHandler.getInstance().handle(msg);
				}
				
				
			}
			
			catch (Exception e) {
				if(e instanceof NullPointerException){
					try {
						client.close();
						ServerScreen.LOGGER.info("Client disconnected " + client.getInetAddress().getHostAddress());
					}catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}else if(e instanceof SocketException){
					break;
				}
				
			}
		}
	}
	
	public String getFromClient() throws IOException{
		String input = "";
			
		input = in.readLine();
		
		
		return input;
		
	}
	
	public void sendToClient(String msg){
		this.out.println(msg);
		this.out.flush();
	}

	@Override
	public void interrupt() {
		super.interrupt();
		try {
			server.close();
			if(client != null)
				client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ServerScreen.LOGGER.info("Server stopped...");
	}

}

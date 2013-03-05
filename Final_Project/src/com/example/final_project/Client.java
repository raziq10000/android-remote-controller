package com.example.final_project;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;



public class Client{


	/**
	 * 
	 */
	
	public InetAddress ia ;
	public Socket socket ;
	private OutputStream output ;	
	private PrintWriter out;
	public boolean isConnected = false;
	
	
	public void connect(String server)  throws Exception {
		ia = InetAddress.getByName(server);
		socket = new Socket(ia, 7878);
		output = socket.getOutputStream();	
		out = new PrintWriter(new BufferedWriter(
			    new OutputStreamWriter(output)), true);
		
		
		
	}
		
	
	public void sendMessage(String msg) {
				out.println(msg);
				
					try {
						if(msg.equals("close"))
							this.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    
	}
	
	public void close() throws Exception{
		isConnected = false;
		socket.close();
		
	}

	

}

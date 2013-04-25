package com.client.final_project;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;

public abstract class Connection {

	public final static int WIFI_CONNECTION = 2, BLUETOOTH_CONNECTION = 3;
			
	protected final String connection_startcode = "a";
	protected final String connection_correction = "f";
    protected  static int connectionType ;
	private static Connection instance;
	protected  static boolean isConnected = false;
	StringBuffer inputBuffer = new StringBuffer();
	
	public static Connection getConnection(int connection_type) {

		if (instance != null && instance.isConnected())
			instance.close();
			
		if (WIFI_CONNECTION == connection_type)
			instance = new WifiConnection();
		else if (BLUETOOTH_CONNECTION == connection_type)
			instance = new BluetoothConnection();
		else
			throw new Error("Wrong Connection Type");	 
			
		return instance;
	}
	public static WifiConnection getWifiConnection() {

		if (instance == null || instance instanceof BluetoothConnection){
				instance = new WifiConnection();			
		}
		
		if(connectionType == BLUETOOTH_CONNECTION)
				return null;
		
		return (WifiConnection)instance;
	}
	public static BluetoothConnection getBluetoothConnection() {

		if (instance == null || instance instanceof WifiConnection){
				instance = new BluetoothConnection();		
		}
		
		if(connectionType == WIFI_CONNECTION)
				return null;
		
		return (BluetoothConnection)instance;
	}
	
	public static Connection getConnection() {
		return instance;
	}

    
	protected PrintWriter output;

	protected BufferedReader input;

	public abstract void connect(String s) throws Exception;

	public abstract void close();

	public abstract OutputStream getOutputStream() throws IOException;

	public abstract InputStream getInputStream() throws IOException;
	
	public abstract   void  sendMessage(String s) throws SocketException, Exception;

	public PrintWriter getWriter() throws IOException {

		return new PrintWriter(getOutputStream());
	}

	protected void intiliazeStreams() throws IOException {
		output = getWriter();
		input = getBufferedReader();
	}

	public BufferedReader getBufferedReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	protected synchronized void sendMsgOutputStream(String s) throws SocketException, Exception {
		output.println(s);		
		output.flush();
		try {
		//	if (output.checkError() == true)
		//		close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	public String readMessage() {
//
//		try {
//			return input.readLine();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	private String readBuffer() {
		String in;
		try {
			while (getInputStream().available() > 0 || inputBuffer.toString().equals(""))
				synchronized (Connection.class) {
					Connection.class.wait();	
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		in = inputBuffer.toString();
		inputBuffer.delete(0, inputBuffer.length());
		
		
		return in;
	}
	
	public int getConnectionType() {
		return connectionType;
	}

	public  boolean isConnected() {
		return isConnected;
	}
	
	public RemoteFile getRemoteFile(String absolutePath) {
		RemoteFile file = null;
		try {
			sendMessage("sendFile/"+absolutePath);
			BufferedReader r = getBufferedReader();
			String jsonStr =  readBuffer();
			Gson gson = new Gson();
			file = gson.fromJson(jsonStr, RemoteFile.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return file;
	}
	

	public synchronized void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	protected void conControlRun() {
		new Thread(new ConnControl()).start();
	}

	class ConnControl implements Runnable {
		String msg = "";
        
		@Override
		public void run() {
			try {
				byte b[] = new byte[1024];
				while (getInputStream().read(b) != -1) {
					msg = new String(b).trim();
					if (msg == null || msg.equals("exit"))
						break;
					   
					inputBuffer = inputBuffer.append(msg);
					if (getInputStream().available() <= 0)
						synchronized (Connection.class) {
							Connection.class.notify();	
						}
						 
					
					for (int i = 0; i < b.length; i++) b[i] = 0;
				}
				setConnected(false);
				close();
			} catch (Exception e) {
				e.printStackTrace();
				setConnected(false);
				close();
				
			}

		}

	}
}

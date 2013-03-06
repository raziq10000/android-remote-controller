package com.example.final_project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;



public class WifiConnection{


	private int searchPort = 7880;
	private int connectionPort = 7878;
	private Map<String, InetAddress> servers;
	private static final String MESSAGE = "HI";
	private InetAddress ia ;
	public Socket socket ;
	private OutputStream output ;	
	private PrintWriter out;
	private BufferedReader in;
	private static WifiConnection instance = new WifiConnection();

	
	private WifiConnection(){
		servers = new HashMap<String, InetAddress>();
	}
	
	public static WifiConnection getInstance(){
		return instance;
	}
	
	public void connect(String server)  throws Exception {
		ia = InetAddress.getByName(server);
		socket = new Socket(ia, connectionPort);
		/*socket.setKeepAlive(true);
		socket.setSoTimeout(2000);*/
		output = socket.getOutputStream();	
		out = new PrintWriter(new BufferedWriter(
			    new OutputStreamWriter(output)), true);		
		new Thread(new ConnControl()).start();
		
	}
	
	public void searchServer(WifiManager wManager){
		servers.clear();
		
		final int BUFFER_SIZE = 1024;
		final int TIMEOUT = 1500;
		try {
			DatagramSocket searchSocket = new DatagramSocket();
			searchSocket.setBroadcast(true);		
			
			byte[] recvBuffer = new byte[BUFFER_SIZE];
			
			searchSocket.setSoTimeout(TIMEOUT);
			sendDiscoveryRequest(searchSocket,wManager);
			Log.v("BeaconInit","Y");
			listenForResponses(searchSocket);
				
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Set<Entry<String, InetAddress>> getSearchResult(){
		return servers.entrySet();
	}
	public void sendMessage(String msg) {
				out.println(msg);
				//out.flush();
				
					try {
						if(out.checkError() == true)
							close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    
	}
	
	public void close() throws Exception{
		socket.close();
		System.out.println("kapandı");
	}
	
	private void sendDiscoveryRequest(DatagramSocket socket, WifiManager wManager) throws IOException {
		    
		InetAddress bcastAddress = getBroadcastAddress(wManager);
		DatagramPacket bcastPacket = new DatagramPacket(MESSAGE.getBytes(), MESSAGE.length(), bcastAddress, searchPort);
		socket.send(bcastPacket);
    }
	
	 private void listenForResponses(DatagramSocket socket) throws IOException {
		    byte[] buf = new byte[1024];
		    try {
		      while (true) {
		    	DatagramPacket recvPacket = new DatagramPacket(buf,buf.length);
		        socket.receive(recvPacket);
		        String recvedString = new String(recvPacket.getData(),0,MESSAGE.length());
		        Log.d("Beacon", "Received response " + recvedString);
		        if(recvedString.equals(MESSAGE)) {
					Log.v("Beacon", "S");
					recvedString = new String(recvPacket.getData(), MESSAGE.length(),recvPacket.getLength() - MESSAGE.length());
					servers.put(recvedString, recvPacket.getAddress());
				}
			
		      }
		    } catch (SocketTimeoutException e) {
		      Log.d("Beacon", "Receive timed out");
		    }
     }

	 private InetAddress getBroadcastAddress(WifiManager wManager) throws IOException {
			DhcpInfo myDhcpInfo = wManager.getDhcpInfo();
			if (myDhcpInfo == null) {
				System.out.println("Could not get broadcast address");
				return null;
			}
			int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)
						| ~myDhcpInfo.netmask;
			byte[] quads = new byte[4];
			for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
			
			return InetAddress.getByAddress(quads);
		}
	/*private class SearchServer implements Runnable{
		private DatagramSocket searchSocket;
		private static final int BUFFER_SIZE = 1024;
		private static final int TIMEOUT = 500;
		public SearchServer() throws SocketException {
			searchSocket = new DatagramSocket();
		}
		public void run() {
			try {
				searchSocket.setBroadcast(true);		
				InetAddress bcastAddress;
				byte [] bcastIp = new byte [] {(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
				bcastAddress = InetAddress.getByAddress(bcastIp);
				//bcastAddress = InetAddress.getByName("10.0.2.2");
				;
				DatagramPacket bcastPacket = new DatagramPacket(MESSAGE.getBytes(), MESSAGE.length(), bcastAddress, searchPort);
				byte[] recvBuffer = new byte[BUFFER_SIZE];
				DatagramPacket recvPacket = new DatagramPacket(recvBuffer,recvBuffer.length);
				searchSocket.setSoTimeout(TIMEOUT);
				Log.v("BeaconInit","Y");
				while (true) {
					searchSocket.send(bcastPacket);
					try {
						searchSocket.receive(recvPacket);
					} catch (IOException e) {
						if (!e.getClass().equals(java.net.SocketTimeoutException.class)) {
							searchSocket.close();
							return;
						} else {
							continue;
						}
					}
		
					String recvedString = new String(recvPacket.getData(),0,MESSAGE.length());
					if(recvedString.equals(MESSAGE)) {
						Log.v("Beacon", "S");
						recvedString = new String(recvPacket.getData(), MESSAGE.length(),recvPacket.getLength() - MESSAGE.length() - 1);
						servers.put(recvedString, recvPacket.getAddress());
					}
				}
			} catch (SocketException e1) {
				e1.printStackTrace();
			}
			catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}*/

	private class ConnControl implements Runnable{

		String input = "";
		
		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				while(input != null){ 
					input = in.readLine();
				}
				
				socket.close();
				MainActivity.isConnected = false;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} 
		
	}
	

}

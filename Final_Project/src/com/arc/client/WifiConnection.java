package com.arc.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiConnection extends Connection {

		private Socket socket;
		private boolean TCPMode = true;		
		private int connectionPort = AppUtil.TCP_PORT;
		private static int searchPort = AppUtil.UDP_PORT;
		private WifiManager wManager;
		private Map<InetAddress, String> networkofNodes = new HashMap<InetAddress, String>();

		@Override
		public void connect(String server) throws Exception {
			System.out.println("sasdasd");
			InetAddress [] ia = InetAddress.getAllByName(server);
			System.out.println("dddd");
			socket = new Socket(ia[0], connectionPort);
			intiliazeStreams();
			connControlRun();
			connectionType = WIFI_CONNECTION;
			setConnected(true);
		}

		@Override
		public void close() {
			
			try {
				/*try {
					sendMessage("exit");
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				if(socket != null)
					socket.close();
				setConnected(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
     
		}

		@Override
		public OutputStream getOutputStream() throws IOException {

			return socket.getOutputStream();
		}

		@Override
		public InputStream getInputStream() throws IOException {

			return socket.getInputStream();
		}

		
		public void sendMessage(String s) throws Exception {
			if (TCPMode) {
				sendMsgOutputStream(s);
				return;
			}

			DatagramSocket socket = new DatagramSocket();
			DatagramPacket datagrampacket = new DatagramPacket(s.getBytes("UTF-8"),
					s.length(), this.socket.getInetAddress(),searchPort);
			socket.send(datagrampacket);

		}
		public void sendDiscoveryMessages() throws IOException {

			final int TIMEOUT = 5000 ;

			DatagramSocket searchSocket = new DatagramSocket();
			searchSocket.setBroadcast(true);

			searchSocket.setSoTimeout(TIMEOUT);
			sendDiscoveryRequest(searchSocket);
			Log.v("Discovery", "Y");
			listenForResponses(searchSocket);

		}


		private void sendDiscoveryRequest(DatagramSocket socket)
				throws IOException {

			InetAddress bcastAddress = getBroadcastAddress(wManager);
			DatagramPacket bcastPacket = new DatagramPacket(
					CONNECTION_START_CODE.getBytes(),
					CONNECTION_START_CODE.length(), bcastAddress, searchPort);
			
			socket.send(bcastPacket);			
				
		}
		
		public void setUnReliableMode() {
			this.TCPMode = false;
		}
		
		public void setReliableMode(){
			this.TCPMode = true;
		}

		private void listenForResponses(DatagramSocket socket)
				throws IOException {
			byte[] buf = new byte[1024];
			try {
				while (true) {
					DatagramPacket recvPacket = new DatagramPacket(buf,
							buf.length);
					socket.receive(recvPacket);
					String recvedString = new String(recvPacket.getData()).trim();
					String [] tokens = recvedString.split("/");
					Log.d("Discovery", "Received response " + recvedString);
					if (tokens[0].equals(CONNECTION_CORRECTION_CODE)) {
						Log.v("Discovery", "S");
						networkofNodes.put(recvPacket.getAddress(),tokens[1]);
						break;
					}

				}
			} catch (SocketTimeoutException e) {
				Log.d("Discovery", "Receive timed out");
			}
		}

		private InetAddress getBroadcastAddress(WifiManager wManager)
				throws IOException {
			DhcpInfo myDhcpInfo = wManager.getDhcpInfo();
			if (myDhcpInfo == null) {
				Log.w("Discovery", "Could not get broadcast address");
				return null;
			}
			int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)
					| ~myDhcpInfo.netmask;
			byte[] quads = new byte[4];
			for (int k = 0; k < 4; k++)
				quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);

			return InetAddress.getByAddress(quads);
		}

		public void setwManager(WifiManager wManager) {
			this.wManager = wManager;
		}
		
		public WifiManager getwManager() {
			return wManager;
		}

		public Map<InetAddress, String> getNetworkofNodes() {
			return networkofNodes;
		}
		
	}

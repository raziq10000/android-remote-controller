package com.arc.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiConnection extends Connection {

		private Socket socket;

		private boolean TCPMode = true;		

		private int connectionPort = AppUtil.TCP_PORT;
		private static int searchPort = AppUtil.UDP_PORT;

		private WifiManager wManager;
		private List<InetAddress> networkofNodes = new ArrayList<InetAddress>();

		

		@Override
		public void connect(String server) throws Exception {
			InetAddress ia = InetAddress.getByName(server);
			socket = new Socket(ia, connectionPort);
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
				// TODO Auto-generated catch block
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
			Log.v("BeaconInit", "Y");
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
					Log.d("Beacon", "Received response " + recvedString);
					if (recvedString.equals(CONNECTION_CORRECTION_CODE)) {
						Log.v("Beacon", "S");
						networkofNodes.add(recvPacket.getAddress());
						break;
					}

				}
			} catch (SocketTimeoutException e) {
				Log.d("Beacon", "Receive timed out");
			}
		}

		private InetAddress getBroadcastAddress(WifiManager wManager)
				throws IOException {
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

		public void setwManager(WifiManager wManager) {
			this.wManager = wManager;
		}
		
		public WifiManager getwManager() {
			return wManager;
		}

		public List<InetAddress> getNetworkofNodes() {
			return networkofNodes;
		}

	}
package com.client.final_project;

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

		Socket socket;

		boolean reliableMode = true;

		final static int connectionPort = 7878;
		final static int searchPort = 7880;

		private WifiManager wManager;
		List<InetAddress> networkofNodes = new ArrayList<InetAddress>();

		

		@Override
		public void connect(String server) throws Exception {
			InetAddress ia = InetAddress.getByName(server);
			socket = new Socket(ia, connectionPort);
			intiliazeStreams();
			conControlRun();
			connectionType = WIFI_CONNECTION;
		}

		@Override
		public void close() {
			try {
				socket.close();
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

		public void sendDiscoveryMessages() throws IOException {

			final int TIMEOUT = 1500;

			DatagramSocket searchSocket = new DatagramSocket();
			searchSocket.setBroadcast(true);

			searchSocket.setSoTimeout(TIMEOUT);
			sendDiscoveryRequest(searchSocket);
			Log.v("BeaconInit", "Y");
			listenForResponses(searchSocket);

		}

		public void sendMessage(String s) throws Exception {
			if (reliableMode) {
				super.sendMessage(s);
				return;
			}

			DatagramSocket socket = new DatagramSocket();
			DatagramPacket datagrampacket = new DatagramPacket(s.getBytes(),
					s.length(), socket.getInetAddress(), searchPort);
			socket.send(datagrampacket);

		}

		private void sendDiscoveryRequest(DatagramSocket socket)
				throws IOException {

			InetAddress bcastAddress = getBroadcastAddress(wManager);
			DatagramPacket bcastPacket = new DatagramPacket(
					connection_startcode.getBytes(),
					connection_startcode.length(), bcastAddress, searchPort);
			socket.send(bcastPacket);
			listenForResponses(socket);
		}

		private void listenForResponses(DatagramSocket socket)
				throws IOException {
			byte[] buf = new byte[1024];
			try {
				while (true) {
					DatagramPacket recvPacket = new DatagramPacket(buf,
							buf.length);
					socket.receive(recvPacket);
					String recvedString = new String(recvPacket.getData());
					Log.d("Beacon", "Received response " + recvedString);
					if (recvedString.equals(connection_correction)) {
						Log.v("Beacon", "S");
						networkofNodes.add(recvPacket.getAddress());
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

		public void setContext(Context context) {
			wManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
		}
		
		public List<InetAddress> getNetworkofNodes() {
			return networkofNodes;
		}

	}

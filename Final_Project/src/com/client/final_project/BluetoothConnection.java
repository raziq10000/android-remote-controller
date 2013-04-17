package com.client.final_project;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothConnection extends Connection {

		private final UUID searchUuid = UUID
				.fromString("00001101-0000-1000-8000-00805F9B34FB");
		private final UUID connectUuid = UUID
				.fromString("00001101-0000-1000-8000-00805F9B34FB");
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		BluetoothDevice bluetoothDevice;
		BluetoothSocket socket;

		@Override
		public void connect(String server) throws Exception {
			connect(bluetoothAdapter.getRemoteDevice(server));
		}
      
		public void connect(BluetoothDevice device) throws IOException {
			
			this.bluetoothDevice = device;
			socket = device.createRfcommSocketToServiceRecord(connectUuid);
			socket.connect();
			
			if(bluetoothAdapter.isDiscovering())
				bluetoothAdapter.cancelDiscovery();
			intiliazeStreams();
			conControlRun();
			connectionType = BLUETOOTH_CONNECTION;
			setConnected(true);
		}
		
		public boolean isBluetoothOpen() {
		   return bluetoothAdapter.isEnabled();	
		}
		

		@Override
		public void close() {
			try {
				try {
					sendMessage("exit");
				} catch (Exception e) {
					e.printStackTrace();
				}
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

//		public boolean serverConfirm(String server) {
//			return serverConfirm(bluetoothAdapter.getRemoteDevice(server));
//		}
		public boolean startDiscovery() {
			return bluetoothAdapter.startDiscovery();
        }
		
        
//		public boolean serverConfirm(BluetoothDevice server) {
//			final AtomicBoolean isServer = new AtomicBoolean(false);
//			try {
//				socket = server
//						.createInsecureRfcommSocketToServiceRecord(searchUuid);
//				socket.connect();
//				getWriter().write(connection_startcode);
//
//				Thread t = new Thread(new Runnable() {
//					public void run() {
//						try {
//							isServer.set(getBufferedReader().readLine().equals(
//									connection_correction));
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				});
//				t.start();
//				try {
//					Thread.sleep(1200);
//				} catch (InterruptedException e) {
//
//					// e.printStackTrace();
//				}
//				if (t.isAlive())
//					t.interrupt();
//			} catch (IOException e) {
//				try {
//					socket.close();
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//				isServer.set(false);
//			}
//			return isServer.get();
//		}

		@Override
		public  void sendMessage(String s) throws SocketException, Exception {
			sendMsgOutputStream(s);
		}
	}

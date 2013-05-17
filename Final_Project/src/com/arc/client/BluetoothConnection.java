package com.arc.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothConnection extends Connection {

	private final UUID connectUuid = UUID
			.fromString("00002000-0000-1000-8000-00805F9B34FB");
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothSocket socket;

	@Override
	public void connect(String server) throws Exception {
		connect(bluetoothAdapter.getRemoteDevice(server));
	}

	public void connect(BluetoothDevice device) throws IOException {	
		socket = device.createInsecureRfcommSocketToServiceRecord(connectUuid);
		socket.connect();
		if (bluetoothAdapter.isDiscovering())
			bluetoothAdapter.cancelDiscovery();
		intiliazeStreams();
		connControlRun();
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

	/*
	 * public boolean serverConfirm(String server) { return
	 * serverConfirm(bluetoothAdapter.getRemoteDevice(server)); }
	 */
	public boolean startDiscovery() {
		return bluetoothAdapter.startDiscovery();
	}
	
	public boolean cancelDiscovery() {
		return bluetoothAdapter.cancelDiscovery();
	}

	@Override
	public void sendMessage(String s) throws SocketException, Exception {
		sendMsgOutputStream(s);

	}
}

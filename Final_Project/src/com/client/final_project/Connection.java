package com.client.final_project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.final_project.MainActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public abstract class Connection {

	public final static int WIFI_CONNECTÝON = 2, BLUETOOTH_CONNECTÝON = 3;
			
	protected final String connection_startcode = "a";
	protected final String connection_correction = "f";
    protected int connectionType ;
	private static Connection instance;
	private boolean isConnected = false;

	public static Connection getConnection(int connection_type) {

		if (instance == null)
			if (WIFI_CONNECTÝON == connection_type)
				instance = new WifiConnection();
			else if (BLUETOOTH_CONNECTÝON == connection_type)
				instance = new BluetoothConnection();
			else
				throw new Error("Wrong Connection Type");

		return instance;
	}
    
	protected PrintWriter output;

	protected BufferedReader input;

	public abstract void connect(String s) throws Exception;

	public abstract void close();

	public abstract OutputStream getOutputStream() throws IOException;

	public abstract InputStream getInputStream() throws IOException;

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

	public void sendMessage(String s) throws SocketException, Exception {
		output.write(s);
		try {
			if (output.checkError() == true)
				close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String readMessage() {

		try {
			return input.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public int getConnectionType() {
		return connectionType;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	protected void conControlRun() {
		new Thread(new ConnControl()).start();
	}

	class ConnControl implements Runnable {

		String input = "";

		@Override
		public void run() {
			try {

				while (input != null) {
					input = readMessage();
				}

				close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	
	
}

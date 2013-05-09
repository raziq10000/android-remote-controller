package com.arc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

public abstract class Connection {

	public final static int WIFI_CONNECTION = 2, BLUETOOTH_CONNECTION = 3;

	protected final String CONNECTION_START_CODE = "/ARC/";
	protected final String CONNECTION_CORRECTION_CODE = "ARC";
	protected static int connectionType;
	private static Connection instance;
	protected static boolean isConnected = false;
	private StringBuffer inputBuffer = new StringBuffer();

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

		if (instance == null || instance instanceof BluetoothConnection) {
			instance = new WifiConnection();
		}

		if (connectionType == BLUETOOTH_CONNECTION)
			return null;

		return (WifiConnection) instance;
	}

	public static BluetoothConnection getBluetoothConnection() {

		if (instance == null || instance instanceof WifiConnection) {
			instance = new BluetoothConnection();
		}

		if (connectionType == WIFI_CONNECTION)
			return null;

		return (BluetoothConnection) instance;
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

	public abstract void sendMessage(String s) throws SocketException,
			Exception;

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

	protected synchronized void sendMsgOutputStream(String s)
			throws SocketException, Exception {
		output.println(s);
		output.flush();
	}
	
	private String readBuffer() {
		String in;
		try {
			synchronized (read) {
				while (!read.get() || inputBuffer.toString().equals(""))
					read.wait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		in = inputBuffer.toString();
		Log.v("JSON s", in);
		inputBuffer.delete(0, inputBuffer.length());

		return in;
	}

	public int getConnectionType() {
		return connectionType;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public RemoteFile getRemoteFile(String absolutePath) {
		RemoteFile file = null;
		try {
			sendMessage("sendFile/" + absolutePath);
			String jsonStr = readBuffer();
			Gson gson = new Gson();
			file = gson.fromJson(jsonStr, RemoteFile.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}

	public synchronized void setConnected(boolean isConnected) {
		Connection.isConnected = isConnected;
	}

	protected void connControlRun() {
		new Thread(new ConnControl()).start();
	}

	private AtomicBoolean read = new AtomicBoolean(true);

	class ConnControl implements Runnable {
		String msg = "";

		@Override
		public void run() {

			try {
				byte b[] = new byte[1024];
				while (true) {
					if (getInputStream().read(b) == -1) {
						break;
					}
					
					msg = new String(b, "UTF-8").trim();
					Log.v("input message", msg);
					boolean start = msg.indexOf("&") == 0;
					boolean finish = msg.indexOf("?") == msg.length() - 1;
					msg = msg.replace("&", "").replace("?", "");
					synchronized (read) {
						if (start)
							read.set(false);
						inputBuffer = inputBuffer.append(msg);
						if (finish) {
							read.set(!read.get());
							read.notify();
						}
					}
					for (int i = 0; i < b.length; i++)
						b[i] = 0;
				}
				if (!read.get()) {
					read.set(true);
					read.notify();
				}
				close();
				Dashboard.handler.sendEmptyMessage(0);
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}

		}

	}
}

package com.example.final_project;

import java.net.SocketException;

import com.client.final_project.WifiConnection;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.client.final_project.Connection;
import com.example.sensorListerners.DetermineOrientation;
import com.example.sensorListerners.GyroscopeListener;

public class InputActivity extends Activity implements OnTouchListener {

	private Connection conn;
	private SensorManager mgr;
	private Sensor gyro;
	private GyroscopeListener gyroListener;
	private DetermineOrientation dor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		conn = Connection.getConnection();

		if (conn != null && conn.isConnected()) {
			mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
			gyro = mgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			dor = new DetermineOrientation(this, Sensor.TYPE_GRAVITY|Sensor.TYPE_MAGNETIC_FIELD);
			/*
			 * gyroListener = new GyroscopeListener(c);
			 * mgr.registerListener(gyroListener, gyro,
			 * SensorManager.SENSOR_DELAY_NORMAL);
			 */
			if (conn.getConnectionType() == Connection.WIFI_CONNECTION) {
				WifiConnection wifiConnection = Connection.getWifiConnection();
				wifiConnection.setUnReliableMode();
			}
			View v = findViewById(R.id.inputView);
			v.setOnTouchListener(this);

		} else {
			Toast.makeText(this, "You are not connected to any server!",
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_input, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.keyboard) {
			((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
			toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		}

		return super.onOptionsItemSelected(item);
	}
	
	

	int lastX, lastY, x, y, downX, downY;
	boolean scrolling = false;
	long time, lasttime = -1;
	double factor;

	public boolean onTouch(View v, MotionEvent event) {

		if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP
				&& event.getPointerCount() == 2) {
			if (scrolling) {
				scrolling = false;
				if (event.getActionIndex() == 0) {
					lastX = (int) event.getX(1);
					lastY = (int) event.getY(1);
				}

			} else {
				try {
					conn.sendMessage("MOUSE/RIGHT_CLICK/");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			downX = lastX = (int) event.getX();
			downY = lastY = (int) event.getY();

			return true;

		} else if (event.getAction() == MotionEvent.ACTION_POINTER_1_DOWN) {
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			return true;

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (Math.abs((int) event.getX() - downX) < 2
					&& Math.abs((int) event.getY() - downY) < 2) {

				try {
					conn.sendMessage("MOUSE/CLICK/");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			lasttime = -1;
			return false;

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			x = (int) event.getX();
			y = (int) event.getY();

			time = System.currentTimeMillis();
			if (lasttime == -1) {
				factor = 1;
				lasttime = System.currentTimeMillis();
			} else {
				factor = (double) ((Math.sqrt(Math.pow(x - lastX, 2)
						+ Math.pow(y - lastY, 2))) / (time - lasttime));
				if (factor < 1)
					factor = factor + 1;

				lasttime = System.currentTimeMillis();
				System.out.println("asdasdsdada  " + factor);
			}

			if (event.getPointerCount() == 2) {
				scrolling = true;

				try {
					conn.sendMessage("MOUSE/SCROLL/" + (y - lastY) + "/");

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {

				try {
					conn.sendMessage("MOUSE/" + (int) ((x - lastX) * factor)
							+ "/" + (int) ((y - lastY) * factor) + "/");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			lastX = x;
			lastY = y;

			return true;
		}

		return false;
	}

	@Override
	protected void onPause() {
		if (gyroListener != null)
			mgr.unregisterListener(gyroListener, gyro);

		super.onPause();

		/*
		 * if(socket != null) socket.close();
		 */
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent KEvent) {
		
		int keyaction = KEvent.getAction();
		
		if (conn != null && conn.isConnected()) {

			if (keyaction == KeyEvent.ACTION_DOWN) {
				System.out.println("girdi");
				int keycode = KEvent.getKeyCode();

				if (keycode == KeyEvent.KEYCODE_DEL) {
					System.out.println("Deleted");
					try {
						conn.sendMessage("KEY/" + 8 + "/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					int keyunicode = KEvent.getUnicodeChar();
					char character = (char) keyunicode;

					try {
						conn.sendMessage("KEY/" + keyunicode + "/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} else if (keyaction == KeyEvent.ACTION_MULTIPLE) {

			if (conn.isConnected()) {
				String a = KEvent.getCharacters();
				char[] as = a.toCharArray();

				try {
					conn.sendMessage("KEY/" + (int) as[0] + "/");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		return super.dispatchKeyEvent(KEvent);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
        dor.stop();

	}
}

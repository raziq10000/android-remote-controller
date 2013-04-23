package com.example.final_project;

import com.client.final_project.WifiConnection;
import android.app.Activity;
import android.bluetooth.BluetoothClass.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import com.client.final_project.Connection;
import com.example.sensorListerners.DetermineOrientation;
import com.example.sensorListerners.GyroscopeListener;

public class MouseActivity extends Activity implements OnTouchListener {

	private Connection c = Connection.getConnection();
	private SensorManager mgr;
	private Sensor gyro;
	private GyroscopeListener gyroListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mouse);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		if (c != null && c.isConnected()) {
			mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
			
//	       	DetermineOrientation s = new  DetermineOrientation(mgr,Sensor.TYPE_GRAVITY| Sensor.TYPE_MAGNETIC_FIELD);
			gyro = mgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			/*gyroListener = new GyroscopeListener(c);
			mgr.registerListener(gyroListener, gyro,
					SensorManager.SENSOR_DELAY_NORMAL);*/
			if (c.getConnectionType() == Connection.WIFI_CONNECTION) {
				WifiConnection wifiConnection = Connection.getWifiConnection();
				wifiConnection.setUnReliableMode();
			}
			View v = findViewById(R.id.textView1);
			v.setOnTouchListener(this);

		} else {
			Toast.makeText(this, "You are not connected to any server!",
					Toast.LENGTH_LONG).show();
		}
         
		// Intent intent = getIntent();

		// tv.setText(c.ia.getHostName());
		// c.sendMessage("selam ben mouse");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_mouse, menu);
		return true;
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
					c.sendMessage("MOUSE/RIGHT_CLICK/");
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
					c.sendMessage("MOUSE/CLICK/");
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
				if(factor < 1)
					factor = factor + 1;
				
				lasttime = System.currentTimeMillis();
				System.out.println("asdasdsdada  " + factor);
			}

			if (event.getPointerCount() == 2) {
				scrolling = true;

				try {
					c.sendMessage("MOUSE/SCROLL/" + (y - lastY) + "/");

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {

				try {
					c.sendMessage("MOUSE/" + (int)((x - lastX) * factor)
							+ "/" + (int)((y - lastY) * factor) + "/");
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

}

package com.example.final_project;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensorListerners.SensorOrientationListener;

public class MainActivity extends Activity implements SensorEventListener {

	private SensorManager sensorManager;
	private List<Sensor> sensors;
	private EditText et3;
	private static WifiConnection wifiConnection = WifiConnection.getInstance();
	boolean isConnected = false;
	private SensorEventListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

		setContentView(R.layout.activity_main);
		final EditText et = (EditText) findViewById(R.id.editText1);
		et3 = (EditText) findViewById(R.id.editText3);
		Button bt = (Button) findViewById(R.id.button1);
		bt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					if (checkInternetConnection()) {

						if (isConnected == false) {
							wifiConnection.connect(et.getText().toString());
							isConnected = true;
                            listener = new SensorOrientationListener();
							for (Sensor sensor : sensors) {
								if (sensor.getType() == Sensor.TYPE_ACCELEROMETER
										|| sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
										|| sensor.getType() == Sensor.TYPE_GYROSCOPE 
										||   sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
									sensorManager.registerListener(listener,
											sensor,
											SensorManager.SENSOR_DELAY_NORMAL);
							}

						} else
							et3.setText("you are connected");

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					et3.setText(e.toString());
				}
			}
		});

		final EditText et2 = (EditText) findViewById(R.id.editText2);
		bt = (Button) findViewById(R.id.button2);
		bt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// try {
				if (isConnected == true) {
					wifiConnection.sendMessage(et2.getText().toString());
					// if(cl.socket.getChannel().isConnected())
					et2.getEditableText().clear();
				}

				// } catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}
		});

		TextView tv1 = (TextView) findViewById(R.id.textView1);
		tv1.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isConnected) {
					Intent intent = new Intent(MainActivity.this,
							MouseActivity.class);
					startActivity(intent);
				}
			}
		});

		Button scan = (Button) findViewById(R.id.scan);
		scan.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (checkInternetConnection()) {
					wifiConnection
							.searchServer((WifiManager) getSystemService(WIFI_SERVICE));
					Set<Entry<String, InetAddress>> set = wifiConnection
							.getSearchResult();
					Iterator<Entry<String, InetAddress>> i = set.iterator();
					et3.getText().clear();
					while (i.hasNext()) {
						Entry<String, InetAddress> entry = i.next();
						et3.append(entry.getKey() + " "
								+ entry.getValue().getHostAddress() + "\n");
					}
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	protected void onResume() {
		super.onResume();
		// for (Sensor s : sensors)
		// sensorManager.registerListener(this, s,
		// SensorManager.SENSOR_DELAY_NORMAL);

		for (Sensor sensor : sensors) {
			if (sensor.getType() == Sensor.TYPE_ACCELEROMETER
					|| sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
					|| sensor.getType() == Sensor.TYPE_GYROSCOPE
					||   sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
				sensorManager.registerListener(listener, sensor,
						SensorManager.SENSOR_DELAY_NORMAL);
		}

	}

	protected void onPause() {
		super.onPause();
		// client.sendMessage("close");
		sensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void onSensorChanged(SensorEvent event) {
		// if (client != null ){
		// switch (event.sensor.getType()) {
		// case Sensor.TYPE_ROTATION_VECTOR:
		//
		// break;
		//
		// default:
		// break;
		// }
		//
		//
		// client.sendMessage(analogValuesToString(event.sensor.getName(),event.sensor.getType(),
		// event.values));
		// }
	}

	public String analogValuesToString(String name, int type, float[] values) {
		String s = "Sensor " + name + " type " + type + " " + "values:";
		for (float f : values)
			s += f;
		return s;
	}

	private boolean checkInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// test for connection
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			Toast toast = Toast.makeText(this,
					"Check your internet connection!", Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
	}

	/*
	 * public static WifiConnection getClient() { return client; }
	 */

	// public Boolean rotation(float [] values){
	// String msg;
	//
	//
	//
	// cl.sendMessage(msg);
	// }
}

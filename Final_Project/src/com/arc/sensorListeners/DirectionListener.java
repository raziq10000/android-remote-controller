package com.arc.sensorListeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.arc.client.Connection;

public class DirectionListener implements SensorEventListener {

	private Connection conn;
	private long lastTime = -1;

	public DirectionListener(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		long currentTime = System.currentTimeMillis();
		if (lastTime == -1) {
			lastTime = currentTime;
		}

		if (currentTime - lastTime >= 1000) {		
			
			int x = (int) event.values[0];
			int y = (int) event.values[1];
		
			try {
				if (x <= -3 && x >= -6 && y <= 2 && y >= -2) {
					lastTime = currentTime;
					conn.sendMessage("VLC/FORWARD");
				} else if (x <= 7 && x >= 4 && y <= 2 && y >= -2) {
					lastTime = currentTime;
					conn.sendMessage("VLC/REWIND");
				} else if (x <= 2 && x >= -2 && y <= -3 && y >= -6 ) {
					lastTime = currentTime;
					conn.sendMessage("VLC/VOLUMEDOWN");
				} else if (x <= 2 && x >= -2 && y <= 7 && y >= 4) {
					lastTime = currentTime;
					conn.sendMessage("VLC/VOLUMEUP");
				} else {
					lastTime = -1;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}

package com.arc.sensorListeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.arc.client.Connection;

public class UpDownListener implements SensorEventListener {

	private Connection conn;
	private static final double GRAVITY_THRESHOLD = SensorManager.STANDARD_GRAVITY / 2;
	private Boolean up = true;
	
	public UpDownListener(Connection conn){
		this.conn = conn;
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(Connection.getConnection() == null) return;
		if(Sensor.TYPE_GRAVITY == event.sensor.getType()) {
			try {
				if (!up &&  event.values[2] >= GRAVITY_THRESHOLD) {
		        	//Connection.getConnection().sendMessage("face/up");
		        	up = true;
				} else if (up && event.values[2] <= (GRAVITY_THRESHOLD * -1)) {
					conn.sendMessage("VLC/PLAY");
					up = false;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}

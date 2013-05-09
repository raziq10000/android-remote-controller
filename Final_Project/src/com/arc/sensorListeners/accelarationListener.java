package com.arc.sensorListeners;
/*

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class accelarationListener implements SensorEventListener {

	WifiConnection client;
	float last_x, last_y, last_z, lasts_x, lasts_y, lasts_z;
	long lastUpdatedTime = 0;

	public accelarationListener() {
		this.client = WifiConnection.getInstance();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	boolean MoodOn = false;

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (shake(event.values)) {
			MoodOn = !MoodOn;
			client.sendMessage((MoodOn) ? "on" : "off");
		}

		if (MoodOn) {
			float x = event.values[SensorManager.DATA_X], y = event.values[SensorManager.DATA_Y], z = event.values[SensorManager.DATA_Z];
			if (event.timestamp - lastUpdatedTime > 750) {
				if (Math.abs(x - last_x) < 3 && Math.abs(y - last_y) < 3)
					if (Math.abs(z - last_z) > 7)
						client.sendMessage("s/"
								+ ((z - last_z > 0) ? "+" : "-"));

			}
			last_x = x;
			last_y = y;
			last_z = z;
			lastUpdatedTime = event.timestamp;

		}

	}

	long lastUpdated;
	private static final int SHAKE_THRESHOLD = 800;

	public boolean shake(float[] values) {

		boolean is = false;

		long curTime = System.currentTimeMillis();
		// only allow one update every 100ms.
		if ((curTime - lastUpdated) > 100) {
			long diffTime = (curTime - lastUpdated);
			lastUpdated = curTime;
			float x = values[SensorManager.DATA_X], y = values[SensorManager.DATA_Y];

			float speed = Math.abs(x + y - last_x - last_y) / diffTime * 10000;
			is = (speed > SHAKE_THRESHOLD);

			lasts_x = x;
			lasts_y = y;

			return is;
		}
		return is;

	}

}
*/
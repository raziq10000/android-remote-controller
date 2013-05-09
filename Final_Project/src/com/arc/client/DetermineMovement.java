package com.arc.client;

import java.net.SocketException;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class DetermineMovement implements SensorEventListener {

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}
	
	float a = 0.8f;
	float lowPass(float current, float last)
	{
		return last * (1.0f - a) + current * a;
	}

	float origin = 1.0f;
	float threshold = 5.0f;
    long factorUnit = 1000000;
	int xIndex = SensorManager.DATA_X, yIndex = SensorManager.DATA_Y,
			zIndex = SensorManager.DATA_Z;
	float[] values;
	long time = System.nanoTime();
	boolean test = false;
	boolean st1 = false, st2 = false, st3 = false, st4 = false;
	
	float last[] = new float[3];

	@Override
	public void onSensorChanged(SensorEvent event) {

		values = event.values;
		
		for (int i = 0; i < last.length; i++) 
			last[i] = lowPass(values[i], last[i]);
		
		
		values = last;
		int t = 100;
        long current = event.timestamp;
        
        double val = Math.sqrt(values[xIndex] * values[xIndex] + values[zIndex] * values[zIndex]) ;
		
        if (values[xIndex] < -origin && values[zIndex] > origin) {
			st1 =  threshold < val;
			time = event.timestamp;
			return;
		}
			
		if (st1 && values[xIndex] > origin && values[zIndex] > origin && (current - time)/1000000 > t) {
			st2 = threshold < val;
			st1 = false;
			time = event.timestamp;
			return;
		}

		if (st2 && values[xIndex] > origin && values[zIndex] < -origin && (current - time)/1000000 > t) {
			st3 = threshold < val;;
			st2 = false;
			time = event.timestamp;
			return;
		}

		if (st3 && values[xIndex] < -origin && values[zIndex] < -origin && (current - time)/1000000 > t) {
			st4 = threshold < val;;
			st3 = false;
			time = event.timestamp;
			return;
		}

		if (st4) 
			if (Connection.getConnection() != null)
				try {
					Connection.getConnection().sendMessage("daire");
					st4 = false;
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
	}

}

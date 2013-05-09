package com.arc.sensorListeners;

import java.net.SocketException;

import com.arc.client.Connection;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class GyroscopeListener implements SensorEventListener{
	
    private Connection conn;
    
    private static final float MIN_TIME_STEP = (1f / 40f);
    private long mLastTime = System.currentTimeMillis();
    private float mRotationX, mRotationY, mRotationZ;
	
	public GyroscopeListener(Connection conn) {
	    this.conn = conn;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
		
	}

	
	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float angularVelocity = z * 0.96f; // Minor adjustment to avoid drift on Nexus S

        // Calculate time diff
        long now = System.currentTimeMillis();
        float timeDiff = (now - mLastTime) / 1000f;
        mLastTime = now;
        if (timeDiff > 1) {
            // Make sure we don't go bananas after pause/resume
            timeDiff = MIN_TIME_STEP;
        }

        mRotationX += x * timeDiff;
        if (mRotationX > 0.5f)
            mRotationX = 0.5f;
        else if (mRotationX < -0.5f)
            mRotationX = -0.5f;

        mRotationY += y * timeDiff;
        if (mRotationY > 0.5f)
            mRotationY = 0.5f;
        else if (mRotationY < -0.5f)
            mRotationY = -0.5f;

        mRotationZ += angularVelocity * timeDiff;

        try {
			conn.sendMessage(" X: " + mRotationX + " Y : " + mRotationY + " Z : " + mRotationZ);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}

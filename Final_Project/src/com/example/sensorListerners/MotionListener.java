package com.example.sensorListerners;

import com.example.final_project.WifiConnection;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MotionListener implements SensorEventListener {

	private WifiConnection client;

	public MotionListener() {
		this.client = WifiConnection.getInstance();
		
		client.sendMessage("AxisX:" + SensorManager.AXIS_X 
				          +"AxisY:" + SensorManager.AXIS_Y
				          +"AxisZ"  + SensorManager.AXIS_Z + "\n" );
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (client != null) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ROTATION_VECTOR:
				rotation(event);
				break;
			case Sensor.TYPE_ACCELEROMETER:
				// shake(event.values);
				break;
			default:
			}

			// cl.sendMessage(analogValuesToString(event.sensor.getName(),event.sensor.getType(),
			// event.values));
		}
	}

	float[] lastRotationMatrix, angleChange = new float[3];
	float[] rotationMatrix = new float[9];
	long lastTimeRotation = 0;

	private void rotation(SensorEvent event) {
		
		
		if (event.timestamp - lastTimeRotation > 30000) {

			SensorManager.getRotationMatrixFromVector(rotationMatrix,
					event.values);
            float [] outr = new float[9];
            if(SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y,outr)){
			     rotationMatrix = outr;
			     client.sendMessage("remapped");
            }
           if (lastRotationMatrix != null){
				SensorManager.getAngleChange(angleChange, rotationMatrix, lastRotationMatrix);
           }
           else{
        	   lastRotationMatrix = rotationMatrix.clone();
        	   
        	}
            
			
			if(angleChange != null){
				String msg = "rotation ";
				for (int i = 0; i < angleChange.length; i++) 
					 msg += (angleChange[i]*180/Math.PI  + " ") ;
				msg += "\n";
				
				client.sendMessage(msg);
			}
			     
			lastTimeRotation = event.timestamp;
		}
	}

	public String analogValuesToString(String name, int type, float[] values) {
		String s = "Sensor " + name + " type " + type + " " + "values:";
		for (float f : values)
			s += f;
		return s;
	}

	private long lastUpdate = -1;
	private float x, y, z;
	private float last_x, last_y, last_z;
	private static final int SHAKE_THRESHOLD = 800;

	public void shake(float[] values) {

		long curTime = System.currentTimeMillis();
		// only allow one update every 100ms.
		if ((curTime - lastUpdate) > 100) {
			long diffTime = (curTime - lastUpdate);
			lastUpdate = curTime;

			x = values[SensorManager.DATA_X];
			y = values[SensorManager.DATA_Y];
			z = values[SensorManager.DATA_Z];

			float speed = Math.abs(x + y + z - last_x - last_y - last_z)
					/ diffTime * 10000;
			if (speed > SHAKE_THRESHOLD) {
				client.sendMessage("Shake ");
			}
			last_x = x;
			last_y = y;
			last_z = z;

		}
	}

}

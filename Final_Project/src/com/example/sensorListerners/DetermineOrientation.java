package com.example.sensorListerners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.SocketException;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.client.final_project.Connection;

/**
 * Determines whether the device is face up or face down and gives a audio
 * notification (via TTS) when the face-up/face-down orientation changes.
 * @author Adam Stroud &#60;<a href="mailto:adam.stroud@gmail.com">adam.stroud@gmail.com</a>&#62;
 */
public class DetermineOrientation implements SensorEventListener
{
	private  int RATE = SensorManager.SENSOR_DELAY_NORMAL;
	private SensorManager sensorManager;
	private Context context;
    private float[] accelerationValues;
    private float[] magneticValues;
    private int selectedSensorId;
    
	Connection c;
    
   
	public DetermineOrientation(Context context, int t) {
		this.context = context;
		this.sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		selectedSensorId = t;
		c = c.getConnection();
		updateSelectedSensor();
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(context.getExternalCacheDir(), "orientation.csv"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
       @Override
    public void onSensorChanged(SensorEvent event)
    {
        float[] rotationMatrix;
        
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_GRAVITY:
            	accelerationValues = event.values.clone();
                filter(accelerationValues);
                rotationMatrix = generateRotationMatrix();
                if (rotationMatrix != null) {
                	determineOrientation(rotationMatrix);
                }
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accelerationValues = event.values.clone();
                filter(accelerationValues);
                rotationMatrix = generateRotationMatrix();
                
                if (rotationMatrix != null) {
                    determineOrientation(rotationMatrix);
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticValues = event.values.clone();
                rotationMatrix = generateRotationMatrix();
                
                if (rotationMatrix != null) {
                    determineOrientation(rotationMatrix);
                }
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                
                rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(rotationMatrix,
                        event.values);
                determineOrientation(rotationMatrix);
                break;
        }
    }
    private float mLowPassX = 0;
	private float mLowPassY = 0;
	private float mLowPassZ = 0;
	
    private void filter(float[] accelerationValues) {
    	float x = accelerationValues[0];
    	float y = accelerationValues[1];
    	float z = accelerationValues[2];
    	mLowPassX = lowPass(x, mLowPassX);
    	mLowPassY = lowPass(y, mLowPassY);
    	mLowPassZ = lowPass(z, mLowPassZ);
    	accelerationValues[0] = mLowPassX;
    	accelerationValues[1] = mLowPassY;
    	accelerationValues[2] = mLowPassZ;
    }
    float a = 0.1f;
	float lowPass(float current, float last) {
		return last * (1.0f - a) + current * a;
	}

	@Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        
    }
    
    /**
     * Generates a rotation matrix using the member data stored in
     * accelerationValues and magneticValues.
     * 
     * @return The rotation matrix returned from
     * {@link android.hardware.SensorManager#getRotationMatrix(float[], float[], float[], float[])}
     * or <code>null</code> if either <code>accelerationValues</code> or
     * <code>magneticValues</code> is null.
     */
    private float[] generateRotationMatrix()
    {
        float[] rotationMatrix = null;
        
        if (accelerationValues != null && magneticValues != null)
        {
            rotationMatrix = new float[16];
            boolean rotationMatrixGenerated;
            rotationMatrixGenerated =
                    SensorManager.getRotationMatrix(rotationMatrix,
                    null,
                    accelerationValues,
                    magneticValues);
            
            if (!rotationMatrixGenerated)
            {
                rotationMatrix = null;
            }
        }
            
        return rotationMatrix;
    }
    
    /**
     * Uses the last read accelerometer and gravity values to determine if the
     * device is face up or face down.
     * 
     * @param rotationMatrix The rotation matrix to use if the orientation 
     * calculation
     */
    
    float lastTime =0,
    		TIME_TRESHOLD = 1000;
    double lastRoll;
    
    private void determineOrientation(float[] rotationMatrix)
    {
        float[] orientationValues = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientationValues);
        
        double azimuth = Math.toDegrees(orientationValues[0]);
        double pitch = Math.toDegrees(orientationValues[1]);
        double roll = Math.toDegrees(orientationValues[2]);
        float currentTime = System.currentTimeMillis();
        writeFile(azimuth, pitch, roll, currentTime);
        if(currentTime - lastTime > TIME_TRESHOLD) {
        	if(roll > 20 && roll < 30 ) {
        		try {
					c.sendMessage("rollEvent");
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        	
        	lastRoll = roll;
        	lastTime = currentTime;
        }
        	
        
        
    }
    
    PrintWriter writer; 
    
    
    
    private void writeFile(double azimuth, double pitch, double roll,float time) {
    	writer.printf("%f, %f, %f, %f\n",azimuth, pitch, roll, time);
    }
    
    public void stop() {
        writer.close();
	}
    
    
    /**
     * Handler for device being face up.
     */
        
    /**
     * Updates the views for when the selected sensor is changed
     */
    private void updateSelectedSensor()
    {
      
        sensorManager.unregisterListener(this);
        
        // Determine which radio button is currently selected and enable the
        // appropriate sensors
        if (selectedSensorId == (Sensor.TYPE_ACCELEROMETER|Sensor.TYPE_MAGNETIC_FIELD))
        {
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    RATE);
            
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    RATE);
        }
        else if (selectedSensorId == (Sensor.TYPE_GRAVITY|Sensor.TYPE_MAGNETIC_FIELD))
        {
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                    RATE);
            
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    RATE);
        }
        else if ((selectedSensorId == Sensor.TYPE_GRAVITY))
        {
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                    RATE);
        }
        else
        {
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                    RATE);
        }
        
       
       
    }
}

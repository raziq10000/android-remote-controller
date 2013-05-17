package com.arc.client;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.arc.sensorListeners.DirectionListener;
import com.arc.sensorListeners.ShakeListener;
import com.arc.sensorListeners.UpDownListener;

public class VLCActivity extends Activity {
	private ImageButton forwardBt;
	private ImageButton playBt;
	private ImageButton rewindBt;
	private ImageButton stopBt;
	private ImageButton previousBt;
	private ImageButton nextBt;
	private ImageButton fullscreenBt;
	private ImageButton muteBt;
	private ImageButton volumeDownBt;
	private ImageButton volumeUpBt;
	private SensorManager mgr;
	private ShakeListener shakeListener;
	private UpDownListener upDownListener;
	private DirectionListener directionListener;
	private Connection conn;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_vlc);
		AppUtil.CURRENT_CONTEXT = this;
		conn = Connection.getConnection();
		mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		this.playBt = ((ImageButton) findViewById(R.id.playBt));
		this.rewindBt = ((ImageButton) findViewById(R.id.rewindBt));
		this.forwardBt = ((ImageButton) findViewById(R.id.forwardBt));
		this.stopBt = ((ImageButton) findViewById(R.id.stopBt));
		this.previousBt = ((ImageButton) findViewById(R.id.previousBt));
		this.nextBt = ((ImageButton) findViewById(R.id.nextBt));
		this.fullscreenBt = ((ImageButton) findViewById(R.id.fullscreenBt));
		this.muteBt = ((ImageButton) findViewById(R.id.muteBt));
		this.volumeDownBt = ((ImageButton) findViewById(R.id.volumeDownBt));
		this.volumeUpBt = ((ImageButton) findViewById(R.id.volumeUpBt));

		if (conn != null && conn.isConnected()) {

			shakeListener = new ShakeListener(conn);
			upDownListener = new UpDownListener(conn);
			directionListener = new DirectionListener(conn);

			this.playBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/PLAY/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			this.rewindBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/REWIND/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			this.forwardBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/FORWARD/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			this.stopBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/STOP/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			this.previousBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/PREVIOUS/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			this.nextBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/NEXT/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			this.fullscreenBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/FULLSCREEN/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			this.muteBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/MUTE/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			this.volumeDownBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/VOLUMEDOWN/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			this.volumeUpBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/VOLUMEUP/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			Toast.makeText(this, R.string.not_connected_toast ,
					Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		disableMotionControl();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppUtil.CURRENT_CONTEXT = this;
		if(AppUtil.IS_MOTION_ENABLED){
			enableMotionControl();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppUtil.IS_MOTION_ENABLED = false;
	}
	
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {		
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu paramMenu) {
		getMenuInflater().inflate(R.menu.vlc_menu, paramMenu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.touchpad:
			Intent mouseIntent = new Intent(this, InputActivity.class);
			startActivity(mouseIntent);
			break;

		case R.id.motion:
			item.setChecked(!item.isChecked());
			if(item.isChecked()){
				item.setTitle("Disable Motion Control");
				AppUtil.IS_MOTION_ENABLED = true;
				enableMotionControl();
			}else {
				item.setTitle("Enable Motion Control");
				AppUtil.IS_MOTION_ENABLED = false;
				disableMotionControl();
			}
			
	 		break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	private void enableMotionControl(){
		mgr.registerListener(shakeListener,
				mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		mgr.registerListener(upDownListener,
				mgr.getDefaultSensor(Sensor.TYPE_GRAVITY),
				SensorManager.SENSOR_DELAY_GAME);
		
		mgr.registerListener(directionListener,
				mgr.getDefaultSensor(Sensor.TYPE_GRAVITY ),
				SensorManager.SENSOR_DELAY_GAME);
	}
	
	private void disableMotionControl(){
		if(shakeListener != null){
			mgr.unregisterListener(shakeListener);
		}
		if(upDownListener != null){
			mgr.unregisterListener(upDownListener);
		}
		if(directionListener != null){
			mgr.unregisterListener(directionListener);
		}
	}

}
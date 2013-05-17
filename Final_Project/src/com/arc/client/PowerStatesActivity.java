package com.arc.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PowerStatesActivity extends Activity {

	private Button shutdownBt;
	private Button restartBt;
	private Button logoutBt;
	private Button lockBt;
	private Button hibernateBt;
	private Button sleepBt;
	private Connection conn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_ops);
		AppUtil.CURRENT_CONTEXT = this;
		conn = Connection.getConnection();

		shutdownBt = (Button) findViewById(R.id.shutdownBt);
		restartBt = (Button) findViewById(R.id.restartBt);
		logoutBt = (Button) findViewById(R.id.logoutBt);
		lockBt = (Button) findViewById(R.id.lockBt);
		hibernateBt = (Button) findViewById(R.id.hibernateBt);
		sleepBt = (Button) findViewById(R.id.sleepBt);

		if (conn != null && conn.isConnected()) {
			
			shutdownBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("SYS/SHUTDOWN/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			restartBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("SYS/RESTART/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			logoutBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("SYS/LOGOUT/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			lockBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("SYS/LOCK/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			hibernateBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("SYS/HIBERNATE/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			sleepBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("SYS/SLEEP/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			

		} else {
			Toast.makeText(this, R.string.not_connected_toast, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AppUtil.CURRENT_CONTEXT = this;
	}


	
}

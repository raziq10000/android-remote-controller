package com.example.final_project;

import com.client.final_project.Connection;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Dashboard extends Activity {

	private Button exitBt;
	private Button mouseBt;
	private Button wifiBt;
	private Button bltthBt;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		this.wifiBt = (Button)findViewById(R.id.wifiBt);
	    this.mouseBt = (Button)findViewById(R.id.mouseBt);
	    this.exitBt = (Button)findViewById(R.id.exitBt);
	    this.bltthBt = (Button)findViewById(R.id.bluetoothBt);
	    
	    wifiBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//Intent intent = new Intent(Dashboard.this, ScanActivity.class);
				//intent.putExtra("connectionType", Connection.WIFI_CONNECTION);
				Intent intent = new Intent(Dashboard.this,ScanActivity.class);
				intent.putExtra("connectionType", Connection.WIFI_CONNECTION);
				startActivity(intent);			
			}
		});
	    
	    
	    bltthBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Dashboard.this, ScanActivity.class);
				intent.putExtra("connectionType", Connection.BLUETOOTH_CONNECTION);
				startActivity(intent);
			}
		});
	    
	    mouseBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Dashboard.this, MouseActivity.class);
				startActivity(intent);			
			}
		});
	    
	    exitBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try {
					
					finish();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if(MainActivity.isConnected){
				WifiConnection.getInstance().close();
				MainActivity.isConnected = false;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}

package com.example.final_project;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.client.final_project.Connection;
import com.client.final_project.WifiConnection;

public class ScanActivity extends Activity {

	

	private ArrayAdapter  mArrayAdapter;
	
	
	
	int connectionType;
	Connection connection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_layout);
		connectionType =getIntent().getParcelableExtra("connectionType");
		connection = Connection.getConnection(connectionType);
		if(connectionType == Connection.WIFI_CONNECTÝON){
			WifiConnection wifiConnection = (WifiConnection)connection ;
			wifiConnection.setContext(this);
		    try {
				wifiConnection.sendDiscoveryMessages();
				List <InetAddress> servers = wifiConnection.getNetworkofNodes();
				
				
				mArrayAdapter = new ArrayAdapter<T>(this, textViewResourceId); 
			} catch (IOException e) {
                Toast.makeText(this, "Search wireless network is failed", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan, menu);
		return true;
	}

}

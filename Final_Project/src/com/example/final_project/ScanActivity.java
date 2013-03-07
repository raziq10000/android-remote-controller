package com.example.final_project;

import java.io.IOException;
import java.net.InetAddress;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.client.final_project.BluetoothConnection;
import com.client.final_project.Connection;
import com.client.final_project.WifiConnection;

public class ScanActivity extends Activity {

	private ArrayAdapter mArrayAdapter;

	int connectionType;
	Connection connection;

	Toast t = Toast.makeText(this, "Server search fail", Toast.LENGTH_LONG);
	Toast wififailToast = Toast.makeText(this, "Wifi connection failed", Toast.LENGTH_LONG);
	Toast bluetoothfailToast = Toast.makeText(this, "Bluetooth  connection failed", Toast.LENGTH_LONG);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_layout);
		connectionType = getIntent().getParcelableExtra("connectionType");
		connection = Connection.getConnection(connectionType);
		if (connectionType == Connection.WIFI_CONNECTION) {
			WifiConnection wifiConnection = (WifiConnection) connection;
			wifiConnection.setContext(this);

			mArrayAdapter = new ArrayAdapter<InetAddress>(this, R.layout.deviceitem);
			new discoveryTask().execute(connection);
             
		}
		if(connectionType == Connection.BLUETOOTH_CONNECTION)
		{
			   BluetoothConnection bluetoothConnection = (BluetoothConnection)connection;
			   IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			   registerReceiver(mReceiver, filter);
			
		}
		
		ListView deviceList = (ListView) findViewById(R.id.listofServer);
		deviceList.setAdapter(mArrayAdapter);
		
		
		deviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
			          
				  if(connectionType == Connection.WIFI_CONNECTION){
					  InetAddress server = (InetAddress)mArrayAdapter.getItem(position);
					  try {
						connection.connect(server.getHostAddress());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						wififailToast.show();
					}
					  
				  }
				  if(connectionType == Connection.BLUETOOTH_CONNECTION){
					  BluetoothDevice server = (BluetoothDevice)mArrayAdapter.getItem(position);
					  try {
						connection.connect(server.getAddress());
					} catch (Exception e) {
						bluetoothfailToast.show();
						
					}
				  }
				  
				//write intent activity
			}
		});
	}
	
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				mArrayAdapter.add(device);
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
       if(connectionType == Connection.BLUETOOTH_CONNECTION)
		unregisterReceiver(mReceiver);
	}
	
	class discoveryTask extends AsyncTask<Connection, Void, Void> {

		@Override
		protected Void doInBackground(Connection... params) {
			WifiConnection wifiConnection = (WifiConnection) params[0];
			try {
				wifiConnection.sendDiscoveryMessages();
			} catch (IOException e) {
				t.show();
			}
			for (InetAddress f : wifiConnection.getNetworkofNodes()) 
				mArrayAdapter.add(f);
			
			return null;

		}
	}

}

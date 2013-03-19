package com.example.final_project;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
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

	BluetoothAdapter mBluetoorhAdapter = BluetoothAdapter.getDefaultAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_layout);
		connectionType = getIntent().getExtras().getInt("connectionType");
		connection = Connection.getConnection(connectionType);
		if (connectionType == Connection.WIFI_CONNECTION) {

			WifiManager wManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

			if (!wManager.isWifiEnabled())
				wManager.setWifiEnabled(true);

			WifiConnection wifiConnection = (WifiConnection) connection;
			wifiConnection.setwManager(wManager);

			mArrayAdapter = new ArrayAdapter<InetAddress>(this,
					R.layout.deviceitem);
			new discoveryTask().execute(connection);

		}

		if (connectionType == Connection.BLUETOOTH_CONNECTION) {
			BluetoothConnection bluetoothConnection = (BluetoothConnection) connection;

			if (!bluetoothConnection.isBluetoothOpen()) {
				Toast.makeText(this, "Opening bluetooth", Toast.LENGTH_LONG)
						.show();
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 2);

			}
			mArrayAdapter = new ArrayAdapter<BluetoothDevice>(this,
					R.layout.deviceitem);
			Toast.makeText(this, "Search bluetooth", Toast.LENGTH_LONG).show();
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			registerReceiver(mReceiver, filter);
			bluetoothConnection.startDiscovery();

		}

		ListView deviceList = (ListView) findViewById(R.id.listofServer);
		deviceList.setAdapter(mArrayAdapter);

		deviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {

				if (connectionType == Connection.WIFI_CONNECTION) {
					InetAddress server = (InetAddress) mArrayAdapter
							.getItem(position);
					try {
						Toast.makeText(
								ScanActivity.this,
								"Connection Host Adress :"
										+ server.getHostAddress(),
								Toast.LENGTH_LONG).show();
						connection.connect(server.getHostAddress());
						connection.sendMessage("asdasdasda");

					} catch (Exception e) {

						Toast.makeText(ScanActivity.this,
								"Wifi connection failed", Toast.LENGTH_LONG)
								.show();
					}

				}

				if (connectionType == Connection.BLUETOOTH_CONNECTION) {
					BluetoothDevice server = (BluetoothDevice) mArrayAdapter
							.getItem(position);
					try {
						connection.connect(server.getAddress());
						connection.sendMessage("asdasd");
						BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
					} catch (Exception e) {
						Toast.makeText(ScanActivity.this,
								"Bluetooth  connection failed",
								Toast.LENGTH_LONG).show();
					}
				}

				// write intent activity
			}
		});
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Toast.makeText(ScanActivity.this, "receiver bluetooth",
					Toast.LENGTH_LONG).show();
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
		if (connectionType == Connection.BLUETOOTH_CONNECTION) {
			unregisterReceiver(mReceiver);
			if (mBluetoorhAdapter.isDiscovering())
				mBluetoorhAdapter.cancelDiscovery();

		}

	}

	class discoveryTask extends AsyncTask<Connection, Void, Void> {

		boolean exp = false;

		@Override
		protected Void doInBackground(Connection... params) {
			WifiConnection wifiConnection = (WifiConnection) params[0];
			try {
				wifiConnection.sendDiscoveryMessages();
			} catch (IOException e) {
				exp = true;
			}
			// for (InetAddress f : wifiConnection.getNetworkofNodes())
			// mArrayAdapter.add(f);

			// mArrayAdapter.add("Hiç obje yok");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			WifiConnection wifiConnection = (WifiConnection) WifiConnection
					.getConnection(Connection.WIFI_CONNECTION);
			if (exp)
				Toast.makeText(ScanActivity.this, "Server search fail",
						Toast.LENGTH_LONG).show();
			List<InetAddress> list = wifiConnection.getNetworkofNodes();

			if (list.size() == 0)
				mArrayAdapter.add("Hiç obje yok");
			else
				for (InetAddress device : list)
					mArrayAdapter.add(device);
              
		}
	}
	class Item 
	{
		InetAddress ia ;
		BluetoothDevice device;
		
        public Item(InetAddress ia) {
        	this.ia = ia;
        }
        
        public Item(BluetoothDevice device) {
        	this.device = device;
        }
        
        @Override
        public String toString() {
            if(ia != null)
        	   return ia.getHostName();  
        	else if(device != null) 
        		return device.getName();
        	 else 
        		throw new Error("Item not intiliazed device or InetAdress");
        	
        }
        
        
	}

}



package com.example.final_project;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

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
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
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

	private ArrayAdapter<Item> mArrayAdapter;

	int connectionType;
	Connection connection;

	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_layout);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();
StrictMode.setThreadPolicy(policy);
		connectionType = getIntent().getExtras().getInt("connectionType");
		connection = Connection.getConnection(connectionType);
		if (connectionType == Connection.WIFI_CONNECTION) {
			WifiManager wManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

			if (!wManager.isWifiEnabled())
				wManager.setWifiEnabled(true);

			WifiConnection wifiConnection = (WifiConnection) connection;
			wifiConnection.setwManager(wManager);

			mArrayAdapter = new ArrayAdapter<Item>(this, R.layout.deviceitem);
			mArrayAdapter.setNotifyOnChange(true);
			new discoveryTask().execute(connection);

		}

		if (connectionType == Connection.BLUETOOTH_CONNECTION) {
			BluetoothConnection bluetoothConnection = (BluetoothConnection) connection;
			if (mBluetoothAdapter == null) {
				Toast.makeText(this, "Bluetooth is not supported",
						Toast.LENGTH_LONG).show();
				finish();
				return;
			}
			if (!bluetoothConnection.isBluetoothOpen()) {
				Toast.makeText(this, "Opening bluetooth", Toast.LENGTH_LONG)
						.show();
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 2);
			}
			mArrayAdapter = new ArrayAdapter<Item>(this, R.layout.deviceitem);
			Toast.makeText(this, "Search bluetooth", Toast.LENGTH_LONG).show();
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		//	String action = "android.bleutooth.device.action.UUID";
		//	IntentFilter filter2 = new IntentFilter(action);
//			registerReceiver(mReceiver, filter2);
			registerReceiver(mReceiver, filter);
			bluetoothConnection.startDiscovery();
		}

		ListView deviceList = (ListView) findViewById(R.id.listofServer);
		deviceList.setAdapter(mArrayAdapter);

		deviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				if(connection.isConnected()) return;
				if (connectionType == Connection.WIFI_CONNECTION) {
					InetAddress server = mArrayAdapter.getItem(position).ia;
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
					BluetoothDevice server = mArrayAdapter.getItem(position).device;
					try {
						connection.connect(server.getAddress());
						connection.sendMessage("asdasd");
						BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
						finish();
					} catch (Exception e) {
						Toast.makeText(ScanActivity.this,
								"Bluetooth  connection failed",
								Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				}

			}
		});
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Toast.makeText(ScanActivity.this, "receiver bluetooth",
					Toast.LENGTH_LONG).show();
			Parcelable[] uuidExtra;
			if ("android.bleutooth.device.action.UUID".equals(action)) {
				uuidExtra = intent
						.getParcelableArrayExtra("android.bluetooth.device.extra.UUID");
				if(uuidExtra != null)
					for (Parcelable s : uuidExtra) 
						Log.d("UUID", s.toString());
			}

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				
				for (int i = 0; i < mArrayAdapter.getCount(); i++)
					if (mArrayAdapter.getItem(i).device.equals(device))
						return;
				mArrayAdapter.add(new Item(device));
			}
		}
	};

	public boolean servicesFromDevice(BluetoothDevice device) {

		boolean isAppServer = false;
		Method method;
		ParcelUuid[] retval = null;
		try {

			method = BluetoothDevice.class.getMethod("fetchUuidsWithSdp", null);
			method.invoke(device, null);
			method = BluetoothDevice.class.getMethod("getUuids", null);
			retval = (ParcelUuid[]) method.invoke(device, null);
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		UUID uuid = UUID.fromString("00002000-0000-1000-8000-00805F9B34FB");
		return isAppServer;
	}

	protected void onPause() {
		super.onPause();
		mArrayAdapter.clear();
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
		if (mBluetoothAdapter != null) {
			if (connectionType == Connection.BLUETOOTH_CONNECTION) {
				unregisterReceiver(mReceiver);
				if (mBluetoothAdapter.isDiscovering())
					mBluetoothAdapter.cancelDiscovery();
				mArrayAdapter.clear();
			}
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

			list.clear();
			mArrayAdapter.notifyDataSetChanged();
			if (list.size() == 0)
				mArrayAdapter.add(new Item());
			else {
				for (InetAddress device : list)
					mArrayAdapter.add(new Item(device));
			}

		}
	}

	class Item {
		InetAddress ia;
		BluetoothDevice device;

		public Item() {
		}

		public Item(InetAddress ia) {
			this.ia = ia;
		}

		public Item(BluetoothDevice device) {
			this.device = device;
		}

		@Override
		public String toString() {
			if (ia != null)
				return ia.getCanonicalHostName();
			else if (device != null)
				return device.getName();
			else
				return "Device not found";

		}

	}

}

package com.arc.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ScanActivity extends Activity {

	private ArrayAdapter<String> mArrayAdapter;
	private TextView scanText;
	private Button scanBt;
	private int connectionType;
	private Connection connection;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		connectionType = getIntent().getExtras().getInt("connectionType");
		mArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_item);
		scanText = (TextView) findViewById(R.id.scanText);
		scanBt = (Button) findViewById(R.id.scanBt2);
		connection = Connection.getConnection(connectionType);

		scanBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mArrayAdapter.clear();
				scanText.setText("");
				scan();

			}
		});

		scan();//Scan network
		
		ListView deviceList = (ListView) findViewById(R.id.listofServer);
		deviceList.setAdapter(mArrayAdapter);

		deviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				if (connection.isConnected())
					return;
				if (connectionType == Connection.WIFI_CONNECTION) {
					String server = mArrayAdapter.getItem(position);
					try {
						connection.connect(server);
						Toast.makeText(ScanActivity.this,
								"Connected to host :" + server,
								Toast.LENGTH_SHORT).show();
						finish();
					} catch (Exception e) {
						Toast.makeText(ScanActivity.this,
								"Connection failed!", Toast.LENGTH_LONG)
								.show();
						e.printStackTrace();
					}

				}

				if (connectionType == Connection.BLUETOOTH_CONNECTION) {
					String server = mArrayAdapter.getItem(position);
					try {
						connection.connect(server);
						BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
						finish();
					} catch (Exception e) {
						Toast.makeText(ScanActivity.this,
								"Connection failed",
								Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				}

			}
		});
	}

	private void scan() {
		if (connectionType == Connection.WIFI_CONNECTION) {

			WifiManager wManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiConnection wifiConnection = (WifiConnection) connection;
			wifiConnection.setwManager(wManager);

			// mArrayAdapter = new ArrayAdapter<String>(this,
			// R.layout.device_item);

			new discoveryTask().execute(connection);

		}

		if (connectionType == Connection.BLUETOOTH_CONNECTION) {

			BluetoothConnection bluetoothConnection = (BluetoothConnection) connection;

			if (mBluetoothAdapter == null) {
				Toast.makeText(this,
						"Bluetooth is not supported on your device!",
						Toast.LENGTH_SHORT).show();
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
			// mArrayAdapter = new ArrayAdapter<Item>(this,
			// R.layout.device_item);
			Toast.makeText(this, "Search bluetooth", Toast.LENGTH_LONG).show();
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			// String action = "android.bleutooth.device.action.UUID";
			// IntentFilter filter2 = new IntentFilter(action);
			// registerReceiver(mReceiver, filter2);
			registerReceiver(mReceiver, filter);
			bluetoothConnection.startDiscovery();
		}
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
				if (uuidExtra != null)
					for (Parcelable s : uuidExtra)
						Log.d("UUID", s.toString());
			}

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				for (int i = 0; i < mArrayAdapter.getCount(); i++)
					if (mArrayAdapter.getItem(i).equals(device.getAddress()))
						return;
				mArrayAdapter.add(device.getName());
			}
		}
	};

	/*
	 * public boolean servicesFromDevice(BluetoothDevice device) {
	 * 
	 * boolean isAppServer = false; Method method; ParcelUuid[] retval = null;
	 * try {
	 * 
	 * method = BluetoothDevice.class.getMethod("fetchUuidsWithSdp", null);
	 * method.invoke(device, null); method =
	 * BluetoothDevice.class.getMethod("getUuids", null); retval =
	 * (ParcelUuid[]) method.invoke(device, null); } catch (Exception e) {
	 * 
	 * e.printStackTrace(); } UUID uuid =
	 * UUID.fromString("00002000-0000-1000-8000-00805F9B34FB"); return
	 * isAppServer; }
	 */

	protected void onPause() {
		super.onPause();

	};


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

	private class discoveryTask extends AsyncTask<Connection, Void, Void> {

		boolean exp = false;
		private ProgressDialog pd;
		
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(ScanActivity.this, "", "Scanning", false);
		}

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
			super.onPostExecute(result);
			pd.dismiss();
			
			WifiConnection wifiConnection = Connection.getWifiConnection();
			
			if (exp)
				Toast.makeText(ScanActivity.this, "Server search fail",
						Toast.LENGTH_LONG).show();
			List<InetAddress> list = wifiConnection.getNetworkofNodes();

			if (list.size() == 0)
				scanText.setText("Server not found!");
			else {
				for (InetAddress device : list)
					mArrayAdapter.add(device.getHostName() + " / "
							+ device.getHostAddress());
			}
			list.clear();
		}
	}
	/*
	 * class Item { InetAddress ia; BluetoothDevice device;
	 * 
	 * public Item() { }
	 * 
	 * public Item(InetAddress ia) { this.ia = ia; }
	 * 
	 * public Item(BluetoothDevice device) { this.device = device; }
	 * 
	 * @Override public String toString() { if (ia != null) return
	 * ia.getCanonicalHostName(); else if (device != null) return
	 * device.getName(); else return "Device not found";
	 * 
	 * }
	 * 
	 * }
	 */
}

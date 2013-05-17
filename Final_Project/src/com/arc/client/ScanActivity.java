package com.arc.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

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
				.permitNetwork().build();
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
				mArrayAdapter.notifyDataSetChanged();
				scanText.setText("");
				scan();

			}
		});

		if (connectionType == Connection.BLUETOOTH_CONNECTION) {
			scanBt.setText("Scan for Bluetooth Device");
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			registerReceiver(mReceiver, filter);
			filter = new IntentFilter(
					BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			registerReceiver(mReceiver, filter);
			filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
			registerReceiver(mReceiver, filter);
		}

		scan();// Scan network

		ListView deviceList = (ListView) findViewById(R.id.listofServer);
		deviceList.setAdapter(mArrayAdapter);

		deviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				if (connection.isConnected()) {
					connection.close();
				}

				String server = mArrayAdapter.getItem(position);
				server = server.split("/ ")[1];

				new ConnectionTask(ScanActivity.this).execute(server);
			}
		});
	}

	private void scan() {
		if (connectionType == Connection.WIFI_CONNECTION) {

			WifiManager wManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiConnection wifiConnection = (WifiConnection) connection;
			wifiConnection.setwManager(wManager);

			new DiscoveryTask().execute(connection);

		}

		if (connectionType == Connection.BLUETOOTH_CONNECTION) {

			BluetoothConnection bluetoothConnection = (BluetoothConnection) connection;

			if (mBluetoothAdapter == null) {
				Toast.makeText(this, R.string.bltth_toast, Toast.LENGTH_SHORT)
						.show();
				finish();
				return;
			}
			if (!bluetoothConnection.isBluetoothOpen()) {
				/*
				 * Toast.makeText(this, "Opening bluetooth", Toast.LENGTH_LONG)
				 * .show();
				 */
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 2);
			}

			// Toast.makeText(this, "Search bluetooth",
			// Toast.LENGTH_LONG).show();
			if (mBluetoothAdapter.isDiscovering()) {
				bluetoothConnection.cancelDiscovery();
			}
			bluetoothConnection.startDiscovery();
		}
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		private ProgressDialog pd;

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Parcelable[] uuidExtra;

			if ("android.bluetooth.device.action.UUID".equals(action)) {
				uuidExtra = intent
						.getParcelableArrayExtra("android.bluetooth.device.extra.UUID");
				if (uuidExtra != null)
					for (Parcelable s : uuidExtra)
						Log.d("UUID", s.toString());
			}

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				/*
				 * for (int i = 0; i < mArrayAdapter.getCount(); i++) if
				 * (mArrayAdapter.getItem(i).equals(device.getAddress()))
				 * return;
				 */				
				mArrayAdapter.add(device.getName() + " / "
						+ device.getAddress());
				mArrayAdapter.notifyDataSetChanged();

			}
			
			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				pd.dismiss();
				if(mArrayAdapter.getCount() == 0) {
					scanText.setText("Bluetooth device not found!");
				}
				// Toast.makeText(ScanActivity.this, "Discovery finished",
				// Toast.LENGTH_SHORT).show();
			}

			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				pd = ProgressDialog.show(ScanActivity.this, "", "Scanning...",
						false);
				pd.setCancelable(true);
				// Toast.makeText(ScanActivity.this, "Discovery started",
				// Toast.LENGTH_SHORT).show();
			}
		}

	};

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
				mArrayAdapter.notifyDataSetChanged();
			}
		}
	}

	private class DiscoveryTask extends AsyncTask<Connection, Void, Void> {

		boolean exp = false;
		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(ScanActivity.this, "", "Scanning...",
					false);
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
				Toast.makeText(ScanActivity.this, R.string.scan_failed_toast,
						Toast.LENGTH_LONG).show();
			Map<InetAddress, String> list = wifiConnection.getNetworkofNodes();

			if (list.size() == 0)
				scanText.setText("Server not found!");
			else {
				for (InetAddress device : list.keySet())
					mArrayAdapter.add(list.get(device) + " / "
							+ device.getHostAddress());
			}
			// list.clear();
		}
	}

	private class ConnectionTask extends AsyncTask<String, Void, Void> {

		private ProgressDialog pd;
		private Context mContext;

		public ConnectionTask(Context context) {
			this.mContext = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(mContext, "", "Connecting...", false);
		}

		@Override
		protected Void doInBackground(String... params) {

			try {
				connection.connect(params[0]);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(mContext, R.string.connected_toast,
								Toast.LENGTH_SHORT).show();
					}
				});
				if (connectionType == Connection.BLUETOOTH_CONNECTION) {
					BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
				}
				pd.dismiss();
				finish();
			} catch (Exception e) {
				pd.dismiss();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(mContext, R.string.conn_failed_toast,
								Toast.LENGTH_SHORT).show();

					}
				});
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.dismiss();
		}
	}
}

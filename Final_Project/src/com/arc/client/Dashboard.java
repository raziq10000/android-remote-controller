package com.arc.client;

import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

public class Dashboard extends Activity {

	private Button exitBt;
	private Button remotesListBt;
	private Button wifiBt;
	private Button bltthBt;
	private Connection conn;
	public static Handler handler;
	private PopupWindow popupWindow;
	private static final String IPADDRESS_PATTERN = 
				"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build();
		StrictMode.setThreadPolicy(policy);

		setContentView(R.layout.activity_dashboard);

		AppUtil.CURRENT_CONTEXT = this;
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
				R.drawable.ic_launcher);

		this.wifiBt = (Button) findViewById(R.id.wifiBt);
		this.remotesListBt = (Button) findViewById(R.id.remotesListBt);
		this.exitBt = (Button) findViewById(R.id.exitBt);
		this.bltthBt = (Button) findViewById(R.id.bluetoothBt);

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				final Context currContext = AppUtil.CURRENT_CONTEXT;
				AlertDialog alert = new AlertDialog.Builder(currContext)
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										dialog.dismiss();
										if (!currContext.equals(Dashboard.this)) {
											((Activity) currContext).finish();
										}

									}
								}).create();
				alert.setMessage("Connection to your server has lost!");
				alert.setTitle("Connection Error");
				alert.setIcon(android.R.drawable.stat_notify_error);
				alert.show();
			}

		};

		wifiBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Intent intent = new Intent(Dashboard.this,
				// ScanActivity.class);
				// intent.putExtra("connectionType",
				// Connection.WIFI_CONNECTION);
				LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View popupView = layoutInflater.inflate(R.layout.popup,
						null, true);
				popupWindow = new PopupWindow(popupView, getWindowManager()
						.getDefaultDisplay().getWidth() - 20,
						LayoutParams.WRAP_CONTENT, true);

				popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());

				final EditText ip = (EditText) popupView.findViewById(R.id.ip);
				final Button connectBt = (Button) popupView
						.findViewById(R.id.connectBt);
				final Button searchServerBt = (Button) popupView
						.findViewById(R.id.searchServerBt);

				connectBt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						conn = Connection.getWifiConnection();
		
						/*if (!isWifiEnabled()) { 
							return; 
						}*/
						 
						if(conn.isConnected()){
							conn.close();
						}
						String serverIP = ip.getEditableText().toString();
						
						if(Pattern.matches(IPADDRESS_PATTERN, serverIP)) {
							new ConnectionTask(Dashboard.this).execute(serverIP);
						}
						else {
							Toast.makeText(Dashboard.this, "Please enter valid IP address...", Toast.LENGTH_SHORT).show();
						}
						
						
					}
				});

				searchServerBt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!isWifiEnabled()) {
							return;
						}
						Intent intent = new Intent(Dashboard.this,
								ScanActivity.class);
						intent.putExtra("connectionType",
								Connection.WIFI_CONNECTION);
						startActivity(intent);
						popupWindow.dismiss();
					}
				});

				popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

			}
		});

		bltthBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Dashboard.this, ScanActivity.class);
				intent.putExtra("connectionType",
						Connection.BLUETOOTH_CONNECTION);
				startActivity(intent);
			}
		});

		remotesListBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Dashboard.this,
						RemotesListActivity.class);
				startActivity(intent);
			}
		});

		exitBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			conn = Connection.getConnection();
			if (conn != null && conn.isConnected()) {
				conn.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppUtil.CURRENT_CONTEXT = this;
	}

	private boolean isWifiEnabled() {
		WifiManager wManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (!wManager.isWifiEnabled()) {
			Toast.makeText(Dashboard.this, "Please enable Wifi connection!",
					Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
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
				conn.connect(params[0]);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						popupWindow.dismiss();

						Toast.makeText(mContext, R.string.connected_toast,
								Toast.LENGTH_SHORT).show();
					}
				});
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

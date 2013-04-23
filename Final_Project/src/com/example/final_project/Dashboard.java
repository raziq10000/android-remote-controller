package com.example.final_project;

import com.client.final_project.Connection;
import com.example.final_project.ScanActivity.discoveryTask;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

public class Dashboard extends Activity {

	private Button exitBt;
	private Button remotesListBt;
	private Button wifiBt;
	private Button bltthBt;	
	private Connection conn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_dashboard);
		this.wifiBt = (Button)findViewById(R.id.wifiBt);
	    this.remotesListBt = (Button)findViewById(R.id.remotesListBt);
	    this.exitBt = (Button)findViewById(R.id.exitBt);
	    this.bltthBt = (Button)findViewById(R.id.bluetoothBt);
	    
	    wifiBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//Intent intent = new Intent(Dashboard.this, ScanActivity.class);
				//intent.putExtra("connectionType", Connection.WIFI_CONNECTION);
				
				LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View popupView = layoutInflater.inflate(R.layout.popup, null, true);
				final PopupWindow popupWindow = new PopupWindow(popupView,
						getWindowManager().getDefaultDisplay().getWidth() - 20 , LayoutParams.WRAP_CONTENT, true);

				
				popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				
				final EditText ip = (EditText) popupView.findViewById(R.id.ip);
				final Button connectBt = (Button) popupView.findViewById(R.id.connectBt);
				final Button searchServerBt = (Button) popupView.findViewById(R.id.searchServerBt);

				connectBt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							conn = Connection.getWifiConnection();
							conn.connect(ip.getEditableText().toString());
							conn.sendMessage("asdasdasda");
							popupWindow.dismiss();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

				searchServerBt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Dashboard.this,ScanActivity.class);
						intent.putExtra("connectionType", Connection.WIFI_CONNECTION);
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
				intent.putExtra("connectionType", Connection.BLUETOOTH_CONNECTION);
				startActivity(intent);
			}
		});
	    
	    remotesListBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Dashboard.this, RemotesListActivity.class);
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
	protected void onDestroy() {
		super.onDestroy();
		try {
			conn = Connection.getConnection();
			if(conn != null && conn.isConnected()){
				conn.close();
				conn.setConnected(false);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}

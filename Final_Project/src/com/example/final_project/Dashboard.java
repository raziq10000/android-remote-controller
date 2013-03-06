package com.example.final_project;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		this.wifiBt = (Button)findViewById(R.id.wifiBt);
	    this.mouseBt = (Button)findViewById(R.id.mouseBt);
	    this.exitBt = (Button)findViewById(R.id.exitBt);
	    
	    wifiBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Dashboard.this, MainActivity.class);
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
					WifiConnection.getInstance().close();
					MainActivity.isConnected = false;
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

}

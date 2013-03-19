package com.example.final_project;


import com.client.final_project.WifiConnection;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;
import com.client.final_project.Connection;

public class MouseActivity extends Activity implements OnTouchListener{

		Connection c = Connection.getConnection();
		
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        if(c.isConnected()){
        	if (c.getConnectionType() == Connection.WIFI_CONNECTION) {
        		WifiConnection wifiConnection = Connection.getWifiConnection();
        		wifiConnection.setUnReliableMode();
        	}
            View v = findViewById(R.id.textView1);
            v.setOnTouchListener(this);
            
        }else {
        	Toast.makeText(this, "Not connected..", 0).show();
		}
        
       // Intent intent = getIntent();
       
        //tv.setText(c.ia.getHostName());
        //c.sendMessage("selam ben mouse");
       
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_mouse, menu);
        return true;
    }
    
    private long lastUpdate = -1, down_time = -1;
    int lastX, lastY, x, y, downX, downY;
   // boolean taken = false;

	public boolean onTouch(View v, MotionEvent event) {
		long curTime = event.getEventTime();
		
		
		if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP && event.getPointerCount() == 2){
			
			try {
				c.sendMessage("MOUSE/RIGHT_CLICK/");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}else
		

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				down_time = curTime;
				lastUpdate = curTime;
				downX = lastX = (int)event.getX();
				downY = lastY = (int)event.getY();
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {

				if (Math.abs((int)event.getX() - downX) < 2
						&& Math.abs((int)event.getY() - downY) < 2 ){
					
					try {
						c.sendMessage("MOUSE/CLICK/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
					
				return false;
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
	
				if (event.getAction() == MotionEvent.ACTION_MOVE && event.getPointerCount() == 2) {
					TextView a = (TextView)findViewById(R.id.textView1);
					a.append("scroll " + event.getY() + "\n");
				}else{
					lastUpdate = curTime;
					x = (int)event.getX();
					y = (int)event.getY();
					
					try {
						c.sendMessage("MOUSE/" + (x - lastX) + "/" + (y - lastY) + "/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					lastX = x;
					lastY = y;
				}
					return true;
			}
		
			
		
		return false;
	}

	

	@Override
	protected void onPause() {
		super.onPause();
		c.close();
	}
	
	
	
}

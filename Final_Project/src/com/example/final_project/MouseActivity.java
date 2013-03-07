package com.example.final_project;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

public class MouseActivity extends Activity implements OnTouchListener{

		WifiConnection c;
		private DatagramSocket socket;
		private DatagramPacket packet;
		private InetAddress addr;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        if(MainActivity.isConnected){
        	addr = WifiConnection.getInstance().socket.getInetAddress();
            try {
    			socket = new DatagramSocket(7880);
    		} catch (SocketException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
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
				socket.send(new DatagramPacket("MOUSE/RIGHT_CLICK/".getBytes(),"MOUSE/RIGHT_CLICK/".length(), addr,7880));
			} catch (IOException e) {
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
						socket.send(new DatagramPacket("MOUSE/CLICK/".getBytes(),"MOUSE/CLICK/".length(), addr, 7880));
					} catch (IOException e) {
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
						socket.send(new DatagramPacket(("MOUSE/" + (x - lastX) + "/" + (y - lastY) + "/").getBytes(),("MOUSE/" + (x - lastX) + "/" + (y - lastY) + "/").length(), addr, 7880));
					} catch (IOException e) {
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
		
		if(socket != null)
			socket.close();
	}
	
	
	
}

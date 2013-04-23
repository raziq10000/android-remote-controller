package com.example.final_project;

import java.net.SocketException;

import com.client.final_project.Connection;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;

public class KeyboardActivity extends Activity {

	private Connection conn = Connection.getConnection();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keyboard);
		
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
		toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		
		
	}
	
	
/*	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		System.out.println(event.getNumber());
		
		
		return super.onKeyDown(keyCode, event);
	}

*/

	@Override
	public boolean dispatchKeyEvent(KeyEvent KEvent) 
	{
	    int keyaction = KEvent.getAction();
	    
	    if(keyaction == KeyEvent.ACTION_DOWN)
	    {
	    	 System.out.println("girdi");
	        int keycode = KEvent.getKeyCode();
	        
	        if (keycode == KeyEvent.KEYCODE_DEL){
	        	System.out.println("Deleted");
	        	try {
					conn.sendMessage("KEY/" + 8 + "/");
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } else {
	            int keyunicode = KEvent.getUnicodeChar();
		        char character = (char) keyunicode;
		        if(conn.isConnected()){
		        	try {
						conn.sendMessage("KEY/" + keyunicode + "/");
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		        System.out.println("DEBUG MESSAGE KEY=" + (int)character + " KEYCODE=" +  keyunicode);
	        }
	
	        
	    } else if(keyaction == KeyEvent.ACTION_MULTIPLE)
	    {
	    	 String a = KEvent.getCharacters();
	    	 char[] as = a.toCharArray();
	    	 
	    	 
	    	 if(conn.isConnected()){
		        	try {
						conn.sendMessage("KEY/" + (int)as[0] + "/");
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
	        
	    }


	    return super.dispatchKeyEvent(KEvent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.keyboard, menu);
		return true;
	}

}

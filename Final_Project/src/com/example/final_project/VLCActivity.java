package com.example.final_project;

import java.net.SocketException;

import com.client.final_project.Connection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class VLCActivity extends Activity {
	private ImageButton forwardBt;
	private ImageButton playBt;
	private ImageButton rewindBt;
	private ImageButton stopBt;
	private ImageButton previousBt;
	private ImageButton nextBt;
	private ImageButton fullscreenBt;
	private ImageButton muteBt;
	private ImageButton volumeDownBt;
	private ImageButton volumeUpBt;
	
	private Connection conn;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_vlc);
		conn = Connection.getConnection();

		this.playBt = ((ImageButton) findViewById(R.id.playBt));
		this.rewindBt = ((ImageButton) findViewById(R.id.rewindBt));
		this.forwardBt = ((ImageButton) findViewById(R.id.forwardBt));
		this.stopBt = ((ImageButton) findViewById(R.id.stopBt));
		this.previousBt = ((ImageButton) findViewById(R.id.previousBt));
		this.nextBt = ((ImageButton) findViewById(R.id.nextBt));
		this.fullscreenBt = ((ImageButton) findViewById(R.id.fullscreenBt));
		this.muteBt = ((ImageButton) findViewById(R.id.muteBt));
		this.volumeDownBt = ((ImageButton) findViewById(R.id.volumeDownBt));
		this.volumeUpBt = ((ImageButton) findViewById(R.id.volumeUpBt));

		if (conn != null && conn.isConnected()) {
			this.playBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/PLAY/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			this.rewindBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/REWIND/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			this.forwardBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/FORWARD/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			this.stopBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/STOP/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			this.previousBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/PREVIOUS/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			this.nextBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/NEXT/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			this.fullscreenBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/FULLSCREEN/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			this.muteBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/MUTE/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			this.volumeDownBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/VOLUMEDOWN/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			this.volumeUpBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("VLC/VOLUMEUP/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} else {
			Toast.makeText(this, "You are not connected to any server!",
					Toast.LENGTH_LONG).show();
		}
			
	}

	public boolean onCreateOptionsMenu(Menu paramMenu) {
		getMenuInflater().inflate(R.menu.activity_vlc, paramMenu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.touchpad) {
			Intent mouseIntent = new Intent(this,
					InputActivity.class);
			startActivity(mouseIntent);
		}

		return super.onOptionsItemSelected(item);
	}

}
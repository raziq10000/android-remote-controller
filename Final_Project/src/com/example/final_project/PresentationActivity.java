package com.example.final_project;

import java.net.SocketException;

import com.client.final_project.Connection;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PresentationActivity extends Activity {

	private Button nextBt;
	private Button previousBt;
	private Button gotoBt;
	private Button startBt;
	private Button finishBt;
	private Connection conn;
	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_presentation);
		conn = Connection.getConnection();

		nextBt = (Button) findViewById(R.id.nextBt);
		previousBt = (Button) findViewById(R.id.previousBt);
		gotoBt = (Button) findViewById(R.id.gotoBt);
		startBt = (Button) findViewById(R.id.startBt);
		finishBt = (Button) findViewById(R.id.finishBt);

		if (conn != null && conn.isConnected()) {
			
			startBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("PPT/START/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			nextBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("PPT/NEXT/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			previousBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("PPT/PREVIOUS/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			gotoBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {
					
						LayoutInflater li = LayoutInflater.from(context);
						View promptView = li.inflate(R.layout.prompt, null);

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

						// set prompts.xml to alertdialog builder
						alertDialogBuilder.setView(promptView);
						
						final EditText userInput = (EditText) promptView.findViewById(R.id.slideNo);	
						
						// set dialog message
						alertDialogBuilder
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// get user input and set it to
												// result
												// edit text
												try {
													conn.sendMessage("PPT/GOTO/" + userInput.getText() + "/");
												} catch (SocketException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												} catch (Exception e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
										})
								.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});

						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();

						// show it
						alertDialog.show();
						
					
				}
			});
			
			finishBt.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {

					try {
						conn.sendMessage("PPT/FINISH/");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

		} else {
			Toast.makeText(this, "You are not connected to any server!", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_presentation, menu);
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

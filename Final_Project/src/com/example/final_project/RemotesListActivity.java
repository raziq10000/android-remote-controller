package com.example.final_project;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.client.final_project.Connection;
import com.client.final_project.RemoteFile;

public class RemotesListActivity extends ListActivity {

	static final String[] PROGRAMS = new String[] { "Touchpad & Keyboard", 
			"VLC Media Player", "Presentation", "System","Choose file"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_remotes_list);

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, PROGRAMS));

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = null;
				
				switch (position) {
				case 0:
					intent = new Intent(RemotesListActivity.this, InputActivity.class);
					break;
				case 1:
					intent = new Intent(RemotesListActivity.this, VLCActivity.class);
					break;
				case 2:
					intent = new Intent(RemotesListActivity.this, PresentationActivity.class);
					break;
				case 3:
					intent = new Intent(RemotesListActivity.this, SystemOpsActivity.class);
					break;
				case 4:
					Connection c = Connection.getConnection();
					if(c.isConnected()) {
						RemoteFile file = c.getRemoteFile("C:\\");
						if(file != null)
							for(RemoteFile f:file.getSubfiles())
								Log.v("rf",f.getAbsolutePath());
					}
					break;
				default:
					break;
				}
				
				if(intent != null)
					startActivity(intent);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_remotes_list, menu);
		return true;
	}

}

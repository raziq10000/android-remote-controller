package com.arc.client;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RemotesListActivity extends ListActivity {

	static final String[] PROGRAMS = new String[] { "Touchpad & Keyboard",
			"VLC Media Player", "Presentation", "Power States", "File Browser" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_remotes_list);
		AppUtil.CURRENT_CONTEXT = this;
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
					intent = new Intent(RemotesListActivity.this,
							InputActivity.class);
					break;
				case 1:
					intent = new Intent(RemotesListActivity.this,
							VLCActivity.class);
					break;
				case 2:
					intent = new Intent(RemotesListActivity.this,
							PresentationActivity.class);
					break;
				case 3:
					intent = new Intent(RemotesListActivity.this,
							PowerStatesActivity.class);
					break;
				case 4:
					intent = new Intent(RemotesListActivity.this,
							FileBrowserActivity.class);						
					break;
				}

				if (intent != null)
					startActivity(intent);

			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AppUtil.CURRENT_CONTEXT = this;
	}

}

package com.example.final_project;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.client.final_project.Connection;
import com.client.final_project.RemoteFile;


public class RemoteFileBrowser extends Activity {

	
	public static final String TAG = RemoteFileBrowser.class.getSimpleName();
	public static final String START_PATH = "start_path";

	// If you want to access system folders(for example on rooted device) change
	// HOME constant;
	private static  RemoteFile HOME;
	private RemoteFile mPath;
	private ArrayList<RemoteFile>  mBacklist;
	private LoaderListener mLoderListener = new LoaderListener();
	private FileListAdapter mAdapter;
	private TextView mPathText;
	private ImageButton mBackBtn;
	private ImageButton mHomeBtn;
	private ImageButton mCloseBtn;
	private ViewSwitcher mViewSwitcher;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.filechooser_activity_file_chooser);
		
		if(Connection.getConnection() == null  || !Connection.getConnection().isConnected()) {
			Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT);
			finish();
		}
		if(HOME == null)
		 HOME = Connection.getConnection().getRemoteFile("HOME");

		if (null != getIntent().getExtras()) {
			String path = getIntent().getExtras().getString(START_PATH);

			if (!TextUtils.isEmpty(path)) {
				mPath = Connection.getConnection().getRemoteFile(path);
			}
		}
		if (null != savedInstanceState) {
			String path = savedInstanceState.getString(START_PATH);
			if (!TextUtils.isEmpty(path)) {
				mPath = Connection.getConnection().getRemoteFile(path);
			}
		}

		
		if(mPath == null)
			mPath = HOME;
		
		mBacklist = new ArrayList<RemoteFile>();
		
		mCloseBtn = (ImageButton) findViewById(R.id.close_button);
		mCloseBtn.setOnClickListener(new CloseListener());
		mBackBtn = (ImageButton) findViewById(R.id.back_button);
		mBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPath = mBacklist.remove(mBacklist.size() - 1);
				load();
			}
		});
		
		mViewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher);
		mPathText = (TextView) findViewById(R.id.path);
		mHomeBtn = (ImageButton) findViewById(R.id.home_button);
		mHomeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mBacklist.add(mPath);
				mPath = HOME;
				load();
			}
		});
		
		ListView list = (ListView) findViewById(R.id.list);
		mAdapter = new FileListAdapter(getApplicationContext());
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new SelectionListener());

		load();
	}
	
	@Override
	public void onBackPressed() {
		if (mBacklist.size() != 0) {
			mPath = mBacklist.remove(mBacklist.size() - 1);
			load();
		} else {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(START_PATH, mPath.getAbsolutePath());
		super.onSaveInstanceState(outState);
	}
	


	
	private class CloseListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			setResult(Activity.RESULT_CANCELED);
			finish();

		}

	}
	
	
	private class SelectionListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mBacklist.add(mPath);
			mPath = (RemoteFile) mAdapter.getItem(position);
			
			if (mPath.isDirectory()) {
				load();
			} else {
				Intent data = new Intent();
				// this code going to change
				data.setData(Uri.parse(mPath.getAbsolutePath()));
				setResult(Activity.RESULT_OK, data);
				finish();
			}
		}
	}
	
	
	private void load() {
		new PathLoader(mLoderListener).execute(mPath);
}
	
	private class LoaderListener  {
		
		public void onLoading() {
			mViewSwitcher.showNext();
		}

		
		public void onPathLoaded(List<RemoteFile> files) {
			mAdapter.setObjects(files);
			mPathText.setText(mPath.getAbsolutePath());
			if (mPath.equals(HOME)) {
				mBackBtn.setEnabled(false);
				mHomeBtn.setEnabled(false);
			} else {
				mBackBtn.setEnabled(true);
				mHomeBtn.setEnabled(true);
			}

			mViewSwitcher.showPrevious();
		}

	}
	
	
	private static class PathLoader extends AsyncTask<RemoteFile, Void, List<RemoteFile>> {

		private LoaderListener mListener;

		public PathLoader(LoaderListener listener) {
		      this.mListener = listener;	
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mListener.onLoading();
		}

		@Override
		protected List<RemoteFile> doInBackground(RemoteFile... params) {
			ArrayList<RemoteFile> result = new ArrayList<RemoteFile>();
			if (params.length != 1) {
				Log.e(TAG, "Could not load directory with empty path");
				return result;
			}

			Connection c = Connection.getConnection();
			if(c != null && c.isConnected() && !params[0].isIntiliazed()) 
				 params[0].setSubfiles(c.getRemoteFile(params[0].getAbsolutePath()).getSubfiles());
				 
			result = params[0].getSubfiles();
			
				
			return result;
		}

		@Override
		protected void onPostExecute(List<RemoteFile> result) {
			super.onPostExecute(result);
			mListener.onPathLoaded(result);

		}

	}

}

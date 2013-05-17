package com.arc.client;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class FileBrowserActivity extends Activity {

	public static final String TAG = FileBrowserActivity.class.getSimpleName();
	public static final String START_PATH = "start_path";
	// If you want to access system folders(for example on rooted device) change
	// HOME constant;
	private static RemoteFile HOME;
	private RemoteFile mPath;
	private ArrayList<RemoteFile> mBacklist;
	private LoaderListener mLoderListener = new LoaderListener();
	private FileListAdapter mAdapter;
	private TextView mPathText;
	private ImageButton mBackBtn;
	private ImageButton mHomeBtn;
	private ImageButton mParentBtn;
	private ImageButton mCloseBtn;
	private ViewSwitcher mViewSwitcher;
	private ListView list;
	private Connection conn;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_file_browser);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		AppUtil.CURRENT_CONTEXT = this;
		conn = Connection.getConnection();
		mCloseBtn = (ImageButton) findViewById(R.id.close_button);
		mBackBtn = (ImageButton) findViewById(R.id.back_button);
		mParentBtn = (ImageButton) findViewById(R.id.parentDirectory_button);
		mHomeBtn = (ImageButton) findViewById(R.id.home_button);

		if (conn != null && conn.isConnected()) {
			if (HOME == null)
				HOME = conn.getRemoteFile("HOME");

			if (mPath == null)
				mPath = HOME;

			mBacklist = new ArrayList<RemoteFile>();

			mCloseBtn.setOnClickListener(new CloseListener());

			mBackBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mPath = mBacklist.remove(mBacklist.size() - 1);
					load();
				}
			});

			mViewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher);
			mPathText = (TextView) findViewById(R.id.path);

			mHomeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mBacklist.add(mPath);
					mPath = HOME;
					load();
				}
			});

			mParentBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					RemoteFile rf = mPath.getParent();
					if (rf == null)
						return;
					mBacklist.add(mPath);
					mPath = rf;
					for (RemoteFile f : mBacklist)
						if (f.getAbsolutePath().equals(mPath.getAbsolutePath())) {
							mPath = f;
							break;
						}
					load();
				}
			});
			list = (ListView) findViewById(R.id.list);
			mAdapter = new FileListAdapter(getApplicationContext());
			list.setAdapter(mAdapter);
			list.setOnItemClickListener(new SelectionListener());

			load();

		} else {
			mBackBtn.setEnabled(false);
			mCloseBtn.setEnabled(false);
			mHomeBtn.setEnabled(false);
			mParentBtn.setEnabled(false);
			Toast.makeText(this, R.string.not_connected_toast,
					Toast.LENGTH_SHORT).show();
		}

		/*
		 * if (null != getIntent().getExtras()) { String path =
		 * getIntent().getExtras().getString(START_PATH);
		 * 
		 * if (!TextUtils.isEmpty(path)) { mPath =
		 * Connection.getConnection().getRemoteFile(path); } } if (null !=
		 * savedInstanceState) { String path =
		 * savedInstanceState.getString(START_PATH); if
		 * (!TextUtils.isEmpty(path)) { mPath =
		 * Connection.getConnection().getRemoteFile(path); } }
		 */

	}

	@Override
	public void onBackPressed() {
		if (conn != null && conn.isConnected() && mBacklist.size() != 0) {
			mPath = mBacklist.remove(mBacklist.size() - 1);
			load();
		} else {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	}

	private class CloseListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			setResult(Activity.RESULT_CANCELED);
			finish();

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private class SelectionListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			RemoteFile select = (RemoteFile) mAdapter.getItem(position);

			if (select.isDirectory()) {
				mBacklist.add(mPath);
				mPath = select;
				load();
			} else {

				try {
					conn.sendMessage("openFile/" + select.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Intent data = new Intent();
				// // this code going to change
				// data.setData(Uri.parse(mPath.getAbsolutePath()));
				// setResult(Activity.RESULT_OK, data);
				// finish();
			}
		}
	}

	private void load() {
		new PathLoader(mLoderListener).execute(mPath);
	}
	
	private class LoaderListener {

		public void onLoading() {
			mViewSwitcher.showNext();
		}

		public void onPathLoaded(List<RemoteFile> files) {
			Log.v("RemoteFileBrowser", "pathLoad");
			if (files == null) {
				Toast.makeText(FileBrowserActivity.this, "Couldn't get files!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			mAdapter.setObjects(files);
			mPathText.setText(mPath.getAbsolutePath());
			mPath.setSubfiles(new ArrayList<RemoteFile>(files));
			mParentBtn.setEnabled(mPath.hasParent());
			if (mPath.equals(HOME)) {
				mBackBtn.setEnabled(false);
				mHomeBtn.setEnabled(false);
			} else {
				mBackBtn.setEnabled(true);
				mHomeBtn.setEnabled(true);
			}

			mViewSwitcher.showPrevious();
			list.setSelectionAfterHeaderView();
		}

	}

	private class PathLoader extends
			AsyncTask<RemoteFile, Void, List<RemoteFile>> {

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

			if (!params[0].isIntiliazed())
				params[0] = conn.getRemoteFile(params[0].getAbsolutePath());
			if (params[0] != null)
				return params[0].getSubfiles();
			else
				return null;
		}

		@Override
		protected void onPostExecute(List<RemoteFile> result) {
			super.onPostExecute(result);
			mListener.onPathLoaded(result);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu1, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.touchpad) {
			Intent mouseIntent = new Intent(this, InputActivity.class);
			startActivity(mouseIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppUtil.CURRENT_CONTEXT = this;
	}
	
}
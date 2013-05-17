package com.arc.client;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter {

	private static final String DIR = "<DIR>";

	private static final String FILE = "<FILE>";
	private Context mContext;
	private List<RemoteFile> mObjects = new ArrayList<RemoteFile>();

	public FileListAdapter(Context context) {
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (null == convertView) {
			convertView = View.inflate(mContext, R.layout.file_item, null);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.details1 = (TextView) convertView
					.findViewById(R.id.details1);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		RemoteFile file = mObjects.get(position);
		if (!file.isDirectory()) {
			viewHolder.icon.setImageResource(R.drawable.file);
			viewHolder.details1.setText(FILE);

		} else {
			viewHolder.icon.setImageResource(R.drawable.folder);
			viewHolder.details1.setText(DIR);
		}

		viewHolder.name.setText(file.getName());
		return convertView;
	}

	public void setObjects(List<RemoteFile> objects) {
		mObjects = objects;
		notifyDataSetChanged();
	}

	public void clear() {
		if (mObjects != null)
			mObjects.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mObjects != null)
			return mObjects.size();
		return 1;
	}

	@Override
	public Object getItem(int position) {
		if (mObjects != null)
			return mObjects.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class ViewHolder {
		public ImageView icon;// R.id.icon
		public TextView name;// R.id.name
		public TextView details1;// R.id.details1

	}
}

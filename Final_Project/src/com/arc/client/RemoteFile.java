package com.arc.client;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class RemoteFile {
	private String name, absolutepath;
	private ArrayList<RemoteFile> subfiles;
	private boolean isDirectory;

	public RemoteFile(File f) {
		this.name = f.getName();
		this.absolutepath = f.getAbsolutePath();

		if (f.isDirectory()) {
			isDirectory = true;
			subfiles = new ArrayList<RemoteFile>();
			if (f.listFiles() != null)
				for (File sub : f.listFiles())
					subfiles.add(new RemoteFile(sub.getName(), sub.getAbsolutePath(),sub.isDirectory()));
		}

	}

	private RemoteFile(String name, String absolutePath,boolean isDirectory) {
		this.name = name;
		this.absolutepath = absolutePath;
		this.isDirectory = isDirectory;
	}

	public ArrayList<RemoteFile> getSubfiles() {
		return subfiles;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public boolean isFile() {
		return !isDirectory;
	}

	public boolean isIntiliazed() {
		return subfiles != null;
	}

	public String getName() {
		return name;
	}

	public String getAbsolutePath() {
		return absolutepath;
	}

	public String toString() {
		return (!name.equals("")) ? name : absolutepath;
	}

	public void setSubfiles(ArrayList<RemoteFile> subfiles) {
		this.subfiles = subfiles;
	}
	
	public boolean hasParent() {
		return !(getAbsolutePath().length() <= 3);
	}
	
	public RemoteFile getParent() {
		String name;
		String absolutepath;
		
		
		if(getAbsolutePath().length() <= 3)
			return null;
		int index = getAbsolutePath().lastIndexOf("\\",getAbsolutePath().length() -2);
		if (index == -1)
			index = getAbsolutePath().lastIndexOf("/",getAbsolutePath().length() -2); ;
		if (index == -1)
			return null;
		absolutepath = getAbsolutePath().substring(0, index + 1);
		index = absolutepath.lastIndexOf("\\");
		if (index == -1)
			index = absolutepath.lastIndexOf("/");
		name = absolutepath.substring(index + 1, absolutepath.length());

		return new RemoteFile(name, absolutepath, true);
	}

}
package com.client.final_project;

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
					subfiles.add(new RemoteFile(sub.getName(), sub
							.getAbsolutePath(), this, sub.isDirectory()));
		}

	}

	private RemoteFile(String name, String absolutePath, RemoteFile back,
			boolean isDirectory) {
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

}
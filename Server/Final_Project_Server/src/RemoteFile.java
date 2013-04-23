import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class RemoteFile implements Serializable {
	private static final long serialVersionUID = -7492497492865100102L;
	private String name, absolutepath;
	private ArrayList<RemoteFile> subfiles;
    private boolean isIntiliazed;
	
	public RemoteFile(File f) {
		this.name = f.getName();
		this.absolutepath = f.getAbsolutePath();
		
		if(f.isDirectory()) {
		    subfiles = new ArrayList<RemoteFile>();
			for (File sub : f.listFiles()) 
				subfiles.add(new RemoteFile(sub.getName(), sub.getAbsolutePath()));
		}
		
		isIntiliazed = true;
	}
	
	public RemoteFile(String name,String absolutePath) {
		this.name = name;
		this.absolutepath = absolutePath;
		isIntiliazed = false;
	}

	public ArrayList<RemoteFile> getSubfiles() {
		return subfiles;
	}

	public boolean isDirectory() {
		return  subfiles != null;
	}
	
	public boolean isIntiliazed() {
		return  isIntiliazed;
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
	
	public void intiliaze() {
		subfiles = RemoteFileManager.getSubFiles(absolutepath);
		isIntiliazed = true;
	}
}
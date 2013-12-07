package server;

import java.io.File;
import java.util.List;

public interface WebFile {
	
	public WebFile createWebFile(File f);
	
	public List<String> getFileTypes();

	public String getStatusLine();
	
	public String getEntityHeader();
	
	public File getFile();
}

package webfiles;

import java.io.File;

public interface WebFile {
	
	public List<String> getFileTypes();

	public String getStatusLine();
	
	public String getEntityHeader();
	
	public File getFile();
}

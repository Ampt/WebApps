package server;

import java.io.File;
import java.net.Socket;

public interface WebFile {

	public String getStatusLine();
	
	public String getEntityHeader();
	
	public void sendResource(Socket s);
}

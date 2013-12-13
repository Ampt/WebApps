package webfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import server.GeneratedWebPageFactory;
import server.Types;
import server.WebFile;

@Types(knownTypes = {"jpg", "png", "gif"})
public class ImagePage implements WebFile {

	private File imageFile;
	private WebFile pageNotFound = null;
	
	public ImagePage(File fileToDisplay){
		if(fileToDisplay.exists()){
			imageFile = fileToDisplay;
		} else {
			pageNotFound = new HTMLPage(new File("webapps/404.html"));
		}
	}
	
	@Override
	public String getStatusLine() {
		if(pageNotFound != null){
			return pageNotFound.getStatusLine();
		} else {
			return "HTTP/1.1 200 OK";
			
		}
	}

	@Override
	public String getEntityHeader() {
		if(pageNotFound != null){
			return pageNotFound.getEntityHeader();
		} else {
			String imageName = imageFile.getName();
			String extension = imageName.substring(imageName.lastIndexOf('.')+1);
			if(extension.equalsIgnoreCase("jpeg")){
				extension = "jpg";
			}
			return "Content-Type: image/" + extension;
		}
		
	}

	@Override
	public void sendResource(Socket s) {
		if(pageNotFound != null){
			pageNotFound.sendResource(s);
		} else {
			// Setup a Scanner to read the specified file	
			try {
	
				FileInputStream in = new FileInputStream(imageFile);
				int c;
				while((c = in.read()) != -1) { 
					// keep reading as long as there's something to read
					s.getOutputStream().write(c);
				}
				in.close();
			} catch(IOException e) { 
				System.err.println("File access error. no file sent."); // alt: write message to error log.
			}
		}

	}

}

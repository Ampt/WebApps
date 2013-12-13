package webfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import server.GeneratedWebPageFactory;
import server.Types;
import server.WebFile;

@Types(knownTypes = {"html"})
public class HTMLPage implements WebFile {
	
	private File webPage;
	private WebFile generatedWebPage = null;
	private boolean is404 = false;
	
	public HTMLPage(File fileToDisplay) {
		if(fileToDisplay.exists()){
			webPage = fileToDisplay;
			if(webPage.getName().contains("404.html")){
				is404 = true;
			}
		} else {
			System.out.println("Couldn't find the file in the real files.. checking virtual for: " + fileToDisplay.getName());
			// Let's check if its a known generated page.
			// WOOOO REFLECTION GO:
			generatedWebPage = GeneratedWebPageFactory.getGeneratedFile(fileToDisplay.getName());
		}
	}

	@Override
	public String getStatusLine() {
		if(generatedWebPage != null) {
			return generatedWebPage.getStatusLine();
		} else {
			if(is404) {
				return "HTTP/1.1 404 NOT FOUND";
			} else {
				return "HTTP/1.1 200 OK";
			}
		}
	}

	@Override
	public String getEntityHeader() {
		if(generatedWebPage != null){
			return generatedWebPage.getEntityHeader();
		} else {
			return "Content-Type: text/html";
		}
	}

	@Override
	public void sendResource(Socket client) {
		if(generatedWebPage != null){
			generatedWebPage.sendResource(client);
		} else {
			// Setup a Scanner to read the specified file	
			try {
				PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
				Scanner reader = new Scanner(webPage);
				while(reader.hasNextLine()) { 
					// keep reading as long as there's something to read

					String record = reader.nextLine(); // read a record from the file...
					pw.println(record); // ...and write it to the client
				}
			} catch(IOException e) { 
				System.err.println("File access error. no file sent."); // alt: write message to error log.
			}
		}

	}

}

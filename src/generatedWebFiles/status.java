package generatedWebFiles;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

import server.WebFile;

public class status implements WebFile {
	
	public status(){
		// Nothing to do here.
	}

	@Override
	public String getStatusLine() {
		return "HTTP/1.1 200 OK";
	}

	@Override
	public String getEntityHeader() {
		return "Content-Type: text/html";
	}

	@Override
	public void sendResource(Socket s) {
		// Setup a Scanner to read the specified file	
		try {
			PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
			pw.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
			pw.write("<html><title>Statusy Stuff!</title></head>");
			pw.write("<body>");
			pw.write("<h1 style=\"color: rgb(0, 153, 0);\"> Matt's CS4220 Uber Server <br></h1>");
			pw.write("<h1 style=\"color: rgb(100, 153, 0);\">Written by Matt Dixon (Duh)<br></h1>");
			Date dt = new Date();
			pw.write("<h1 style=\"color: rgb(0, 153, 100);\">Info requested from client IP: " + s.getInetAddress().toString() + " at local time: " + dt.toString() + "<br></h1>");
			pw.write("</body>");
			pw.write("</html>");
			pw.flush();
		
		} catch(IOException e) { 
			System.err.println("File access error. no file sent."); // alt: write message to error log.
		}
	}

}

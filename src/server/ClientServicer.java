package server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientServicer implements Runnable {
	
	private final static int SUCCESS = 0;
	private final static int FAIL = -1;

	private Socket clientSocket;
	private String requestedFile;
	private WebFile webFileRequested;
	
	public ClientServicer(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		int status = receiveRequest(clientSocket);
		// Once we've received the request, send a response.
		if( status == SUCCESS )
			sendResponse( clientSocket );
		try {
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("IO Exception closing client socket. Continuing." );
		}
	}
	
	/**
	 * Listen and receive data on the specified socket stream.
	 * @return FAIL on error; otherwise SUCCESS
	 */
	private int receiveRequest( Socket client ) {
		Scanner sc = null;
		InputStream is = null;
		// Setup a Scanner read data coming from the client
		try {
			is = client.getInputStream();
			sc = new Scanner(is); // open the input stream from the client
			sc.useDelimiter("\n"); // important!
		} catch(IOException e) {
			System.err.println("Can't create client input stream. Aborting client request.");
			return FAIL;
		}
		System.out.println("Client sent the following request: ");
		// Use the Scanner to keep reading until the client get tired of sending...
		String input=null;
		try {
			while ( (input=sc.nextLine() ).length() > 0 ) { // keep reading as long as there is something to read
				//NOTE: This End Of Transmission detection is fairly brittle and only good for 
				//certain requests that are terminated with a CRLF (such as an HTTP GET request).
				//The single CRLF would NOT signify EOT in an HTTP POST request.
				//See http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5
				System.out.println( "   " + input );
				if(input.startsWith("GET")){
					String[] pieces = input.split(" ");
					File f = new File("webapps", pieces[1]);
					webFileRequested = WebFileFactory.getFile(f);
				}
			}
		} catch (NoSuchElementException e ) {
			System.err.println("Error receiving client request. Continuing.");
			return FAIL;
		}

		if( client.isClosed() ) {
			System.out.println("Client closed connection early.");
		}

		// Done receiving from the client.
		System.out.println("Client request received/complete.");
		return SUCCESS;
	}

	/**
	 * Send a simple response on a socket stream to a listening client
	 * @param client the Socket to transmit on
	 * @return FAIL on error
	 */
	private int sendResponse(Socket client) {
		PrintWriter pw = null;
		// Setup a PrintWriter to write to the client
		try {
			pw = new PrintWriter(client.getOutputStream(), true); // open the output stream to the client; autoflush
		} catch( IOException e ) {
			e.printStackTrace();
			System.err.println("Can't create client input stream");
			return FAIL;
		}
		pw.println(webFileRequested.getStatusLine());
		Date dt = new Date();
		pw.println("Date: " + dt.toString()); // general header; see section 4.5
		pw.println(webFileRequested.getEntityHeader());
		pw.println("");
	    pw.flush();
		webFileRequested.sendResource(client);
		return SUCCESS;
	}

}

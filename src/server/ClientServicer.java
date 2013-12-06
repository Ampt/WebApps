package server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientServicer implements Runnable {
	
	private final static int SUCCESS = 0;
	private final static int FAIL = -1;

	private Socket clientSocket;
	
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
		} catch( IOException e ) {
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
			}
		} catch (NoSuchElementException e ) {
			System.err.println("Error receiving client request. Continuing.");
		}

		if( client.isClosed() ) {
			System.out.println("Client closed connection early.");
		} 

		// Done receiving from the client.
		System.out.println("Client request received/complete.");
		sc.close();
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
			System.err.println("Can't create client input stream");
			return FAIL;
		}

		pw.println("Your request is acknowledged.");

		// We're done; shut down the stream and scram.
		pw.close();
		return SUCCESS;
	}

	/**
	 * Send an HTTP response on a socket stream to a listening client
	 * @param client the Socket to transmit on
	 * @return FAIL on error
	 */
	private int sendHTTPResponse(Socket client) {
		PrintWriter pw = null;
		// Setup a PrintWriter to write to the client
		try {
			pw = new PrintWriter(client.getOutputStream(), true); // open the output stream to the client; autoflush
		} catch( IOException e ) {
			System.err.println("Can't create client input stream");
			return FAIL;
		}

		// Write an HTTP header; see http://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html#sec6
		pw.println("HTTP/1.1 200 OK");  // status line; see section 6.1 // TODO: change status based on whether requested resource actually exists
		pw.println("Date: Tue, 15 Jun 2011 08:12:31 GMT"); // general header; see section 4.5 // TODO: Make dynamic
		pw.println("Content-Type: text/html"); // entity header; see section 7.1 // TODO: change Content-Type based on actual content being returned
		pw.println(""); // required CRLF; see section 6

		// Now use a helper method to send the HTML payload from the specified html file.
		sendFile(pw, "webapps/response.html"); // TODO: return the resource actually requested

		// We're done; shut down the stream and scram.
		pw.close();
		return SUCCESS;
	}

	/** Send the contents of the specified file to the client
	 * 
	 * @param pw PrintWriter stream writer to use to send character data
	 * @param fileName file whose contents are to be sent
	 */
	private void sendFile(PrintWriter pw, String fileName ) {
		// Setup a Scanner to read the specified file
		try {
			File f = new File( fileName );
			Scanner reader = new Scanner(f);
			while( reader.hasNextLine() ) { // keep reading as long as there's something to read

				String record = reader.nextLine(); // read a record from the file...
				pw.println( record ); // ...and write it to the client
				//				System.out.println( record );
			}
		} catch( IOException e ) { 
			System.err.println("File access error. no file sent."); // alt: write message to error log.
		}
	}

}

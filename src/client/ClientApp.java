package client;


import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * This app implements the client side of a client/server system.
 * @author hornick
 *
 */
public class ClientApp  {

	private Socket socket; // this client's socket
	private final int port = 8080; // the port to use in the socket
	private PrintWriter pw; // helper class for printing to the output stream
	private Scanner sc;  // helper class for reading from the input stream
	private InetAddress netAddr;
	
	public static void main(String[] args) {
		ClientApp client = new ClientApp();
		client.connect();	// first, make the connection
		client.sendRequest(); // then send a request
	}

	private void connect() {
		//	Create and connect a client-side socket to a pre-existing server socket 
		byte[] addr = {(byte)127,(byte)0,(byte)0, (byte)1}; // 127.0.0.1 is the local loopback IP address

		try{
			netAddr = InetAddress.getByAddress(addr);
			System.out.println("Client: Connecting...");
			socket = new Socket(netAddr, port); // connect to server at this IP address on the specified port
			System.out.println("Client: Connected...");
			pw = new PrintWriter(socket.getOutputStream(), true);  // open the output stream to the server
			sc = new Scanner(socket.getInputStream()); // open the input stream from the server
		} catch( UnknownHostException e ) {
			System.out.println("Client: Unknown host");
			System.exit(1);
		} catch( IOException e ) {
			System.out.println("Client can't connect to server-side socket.");
			System.exit(1);
		}

	}

	/**
	 * Send a request on a socket stream to a listening stream
	 */
	private  void sendRequest() {
		System.out.println("Client: Sending request");
		pw.println("please send money.");
		pause(1000);
		pw.println("");
	}


//	wrap a Thread.sleep() call with try-catch, simplifying the interface
	private void pause(long ms ) { 
		try {
			Thread.sleep(ms);
		}
		catch (InterruptedException e ) {
			// ok to do nothing; we just woke up early
		}
	}
}

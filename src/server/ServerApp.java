package server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This application illustrates the use of sockets
 * to send data between two endpoints. This app implements the server side.
 * @author hornick
 *
 */
public class ServerApp {
	private final static int PORT=8080; // the TCP port to listen on
	private final static int SUCCESS = 0;
	private final static int FAIL = -1;

	private ServerSocket serverSocket; // the server-side socket to listen on
	private ExecutorService threadPool;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String newline = System.getProperty("line.separator");  // on Windows, newline is \r\n (0xD 0xA): CRLF
		ServerApp server = new ServerApp();
		server.init();
		server.startThreads(100);

	}

	/**
	 *  Creates a server-side TCP socket 
	 */
	private void init() {
		try {
			serverSocket = new ServerSocket(PORT); // listen on this port
		} catch( IOException e ) {
			System.err.println("Epic fail: Can't create a server-side socket. Exiting.");
			System.exit(FAIL);
		}
	}
	
	/**
	 * Creates a new threadpool used to service clients.
	 * @param numberOfThreads
	 */
	private void startThreads(int numberOfThreads){
		threadPool = Executors.newFixedThreadPool(numberOfThreads);
		try {
			while(true){
				System.out.println("Waiting on a connection...");
				threadPool.execute(new ClientServicer(serverSocket.accept()));
			}
		} catch (IOException ex){
			System.err.println("IO Exception Encountered on Client Accept! Shutting down.");
			stopThreads();
		}
	}
	
	/**
	 * Stops the thread pool in an orderly fashion. Open connections are
	 * allowed to finish nicely, but no new connections will be accepted.
	 */
	private void stopThreads(){
		threadPool.shutdown();
	}
	
}

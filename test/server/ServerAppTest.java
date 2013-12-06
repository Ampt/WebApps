package server;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerAppTest {
	
	ServerApp sa;

	@Before
	public void setUp() throws Exception {
		sa = new ServerApp();
	}

	@After
	public void tearDown() throws Exception {
		sa = null;
	}

	@Test
	public void testInit(){
		
	}
	
	@Test
	public void testReceiveRequest() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testSendResponse() {
		
	}
	
	@Test
	public void testSendHTTPResponse() {
		
	}

}

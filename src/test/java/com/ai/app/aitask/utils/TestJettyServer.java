package com.ai.app.aitask.utils;


import org.eclipse.jetty.server.Server;

public class TestJettyServer {
	
	Server server = null;
	
	public void start() throws Exception{
		
		server = new Server(3000);
	    server.setHandler(new TestJettyHandle());
	 
	    server.start();
	    //server.join();
	}
	
	public void stop() throws Exception{
		if(server != null)
			server.stop();
	}

}

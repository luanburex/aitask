package com.ai.app.aitask.utils;


import org.eclipse.jetty.server.Server;

public class TestJettyServer {
	
	Server server = null;
	String xml_str = null;
	TestJettyHandle handle = null;
			
	
	public TestJettyServer(String xml_str) {
		super();
		this.xml_str = xml_str;
		handle = new TestJettyHandle(this.xml_str);
	}
	
	public void setTaskXmlStr(String xml){
		this.xml_str = xml;
		handle.setXml_str(xml);
		
	}
	
	public String getTaskXmlStr(){
		return this.xml_str;
	}

	public void start() throws Exception{
		
		server = new Server(3000);
	    server.setHandler(this.handle);
	 
	    server.start();
	    //server.join();
	}
	
	public void stop() throws Exception{
		if(server != null)
			server.stop();
	}

}

package com.ai.app.aitask.utils;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class TestJettyHandle extends AbstractHandler  
{  
	String xml_str = null;
	
    public TestJettyHandle(String xml_str) {
		super();
		this.xml_str = xml_str;
	}
    
    

	public String getXml_str() {
		return xml_str;
	}



	public void setXml_str(String xml_str) {
		this.xml_str = xml_str;
	}



	public void handle(String target,Request baseRequest,HttpServletRequest request,HttpServletResponse response)   
        throws IOException, ServletException  
    {  
    	request.setCharacterEncoding("utf-8");
    	System.out.println(target);
    	if("/tasks".equals(target)){
    		handleTasks(baseRequest, request, response);
    	}else if("/saveResult".equals(target)){
    		handleResult(baseRequest, request, response);
    	}else{
	    	StringBuffer jb = new StringBuffer();
	    	  String line = null;
	    	  try {
	    	    BufferedReader reader = request.getReader();
	    	    while ((line = reader.readLine()) != null)
	    	      jb.append(line);
	    	  } catch (Exception e) { /*report an error*/ }
	    	System.out.println(jb.toString());
	        response.setContentType("text/html;charset=utf-8");  
	        response.setStatus(HttpServletResponse.SC_OK);  
	        baseRequest.setHandled(true);  
	        response.getWriter().println("<h1>Hello World</h1>");  
    	}
    }  
    
    private void handleTasks(Request baseRequest, HttpServletRequest request,HttpServletResponse response) throws IOException{
    	
		response.setContentType("text/html;charset=utf-8");  
	    response.setStatus(HttpServletResponse.SC_OK);  
	    baseRequest.setHandled(true);
	    response.getWriter().println(this.xml_str); 
	    response.getWriter().flush();
    }
    
    
    private void handleResult(Request baseRequest, HttpServletRequest request,HttpServletResponse response) throws IOException{
    	StringBuffer jb = new StringBuffer();
  	  String line = null;
  	  try {
  	    BufferedReader reader = request.getReader();
  	    while ((line = reader.readLine()) != null)
  	      jb.append(line);
  	  } catch (Exception e) { /*report an error*/ }
  	  System.out.println(jb.toString());
      response.setContentType("text/html;charset=utf-8");  
      response.setStatus(HttpServletResponse.SC_OK);  
      baseRequest.setHandled(true);  
      response.getWriter().println(jb.toString());
    }

}

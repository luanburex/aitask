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
    public void handle(String target,Request baseRequest,HttpServletRequest request,HttpServletResponse response)   
        throws IOException, ServletException  
    {  
    	request.setCharacterEncoding("utf-8");
    	System.out.println(target);
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

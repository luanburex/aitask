package com.ai.app.aitask.utils;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class TestJettyServer {
    private AbstractHandler handler;
    private Server          server;
    private int             port;
    public TestJettyServer(int port) {
        this.port = port;
        this.handler = new AbstractHandler() {
            @Override
            public void handle(String u, Request r, HttpServletRequest q, HttpServletResponse p) {
                TestJettyServer.this.handle(u, r, q, p);
            }
        };
    }
    public final void start() throws Exception {
        server = new Server(port);
        server.setHandler(handler);
        server.start();
    }
    public final void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }
    public void handle(String u, Request r, HttpServletRequest q, HttpServletResponse p) {
        p.setContentType("text/html;charset=utf-8");
        p.setStatus(HttpServletResponse.SC_OK);
        r.setHandled(true);
        String content = u;
        Writer writer = null;
        try {
            writer = p.getWriter();
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

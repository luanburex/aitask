package com.ai.app.aitask;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.ai.app.aitask.utils.TestJettyServer;

public class Yummy {

    public static void main(String[] args) {
        TestJettyServer s = new TestJettyServer(5555) {

            @Override
            public void handle(String u, Request r, HttpServletRequest q, HttpServletResponse p) {
                String content = u;
                Writer respWriter = null;
                try {
                    respWriter = p.getWriter();
                    respWriter.write("receive : " + content);
                    respWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        respWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        try {
            s.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

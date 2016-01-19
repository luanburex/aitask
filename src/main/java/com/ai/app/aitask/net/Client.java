package com.ai.app.aitask.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map.Entry;

import javax.servlet.Servlet;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.ai.app.aitask.common.Config;

/**
 * 
 * @author Alex Xu
 *
 */
public class Client {
    protected Logger log = Logger.getLogger(getClass());
    private Server   server;
    private Config   config;
    public Client() {
        this.config = Config.instance("client.properties");
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        for (Entry<Object, Object> servlet : config.getProperties("servlets").entrySet()) {
            log.info(servlet.getKey() + " @ " + servlet.getValue());
            try {
                Class<?> clazz = Class.forName((String) servlet.getKey());
                handler.addServlet(clazz.asSubclass(Servlet.class), (String) servlet.getValue());
            } catch (ClassNotFoundException e) {
                log.error("Servlet not found : " + (String) servlet.getKey());
            }
        }
        int port = Integer.parseInt(config.getProperty(null, "aitask.client.port"));
        log.info("port:" + port);
        server = new Server(port);
        server.setHandler(handler);
    }
    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getHardwareAddress() {
        StringBuffer stringBuffer = new StringBuffer();
        byte[] mac = new byte[0];
        try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            mac = ni.getHardwareAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < mac.length; i++) {
            String string = Integer.toHexString(mac[i] & 0xFF);
            if (i != 0) {
                stringBuffer.append("-");
            }
            if (string.length() == 1) {
                stringBuffer.append(0);
            }
            stringBuffer.append(string);
        }
        if (stringBuffer.length() == 0) {
            return null;
        } else {
            return stringBuffer.toString();
        }
    }
}

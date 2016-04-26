package com.ai.app.aitask.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletMapping;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.xml.sax.SAXException;

import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.common.FileUtil;

/**
 * Simple Servlet Container
 * 
 * @author Alex Xu
 */
public class Client implements Constants {
    protected static final Logger logger = Logger.getLogger(Client.class);
    private Server                server;
    public Client() {
        ServletContextHandler handler = new ServletContextHandler();
        handler.setServer(server = new Server());
        //#com.ai.app.aitask.net.StatusQueryServlet =/status
        try {
            new XmlConfiguration(FileUtil.readStream("client.xml")).configure(handler);
            String path = InetAddress.getLocalHost() + ":" + server.getConnectors()[0].getPort();
            logger.info("Client : " + path + handler.getContextPath());
            for (ServletMapping mapping : handler.getServletHandler().getServletMappings()) {
                logger.info("Servlet : " + mapping.getServletName() + "@" + mapping.getPathSpecs()[0]);
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            server.start();
            logger.info("Client Start.");
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

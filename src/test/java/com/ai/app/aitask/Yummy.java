package com.ai.app.aitask;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.xml.sax.SAXException;

import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.common.FileUtil;

public class Yummy implements Constants {
    boolean good;
    public static void main(String[] args) throws Exception {
        test();
    }

    public static void server() throws SAXException, IOException, Exception {
        //http://www.eclipse.org/jetty/documentation/current/quick-start-configure.html#d0e1602
        //        Object o = new XmlConfiguration(ClassLoader.getSystemResourceAsStream("client.xml")).configure();
        //        ((Server)o).start();
        Server server = new Server();
        Object[] dd = new Object[2];
        Connector c;

        ServletContextHandler handler = new ServletContextHandler();
        handler.setServer(server);
        new XmlConfiguration(FileUtil.readStream("client.xml")).configure(handler);
        for (ServletHolder holder : handler.getServletHandler().getServlets()) {
            System.out.println(holder.getName());
        }
        //        server.start();
    }
    public void dd() {
        ServletContextHandler handler = new ServletContextHandler();
        ServletHolder hoder = new ServletHolder();
        //        Loader.loadClass(clazz)
        Class c = com.ai.app.aitask.net.servlet.TaskArrangeServlet.class;
        Server s = new Server();
        s.setHandler(handler);
    }
    public static void test() {
        int i = 0;
        int j = i;
        i -= i--;
        System.out.println(i);
    }
}
/*
 * 
 * <Call name="addServlet">
 * <Arg>
 * <New class="org.eclipse.jetty.servlet.ServletHolder">
 * <Arg>
 * <New class="com.ai.app.aitask.net.servlet.TaskArrangeServlet" />
 * </Arg>
 * </New>
 * </Arg>
 * <Arg>/arrange</Arg>
 * </Call>
 * <Call name="addServlet">
 * <Arg>
 * <New class="org.eclipse.jetty.servlet.ServletHolder">
 * <Arg>
 * <New class="com.ai.app.aitask.net.servlet.DataQueryServlet" />
 * </Arg>
 * </New>
 * </Arg>
 * <Arg>/data</Arg>
 * </Call>
 * <Call name="addServlet">
 * <Arg>
 * <New class="org.eclipse.jetty.servlet.ServletHolder">
 * <Arg>
 * <New class="com.ai.app.aitask.net.servlet.StepQueryServlet" />
 * </Arg>
 * </New>
 * </Arg>
 * <Arg>/step</Arg>
 * </Call>
 */
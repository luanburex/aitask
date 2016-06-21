package com.ai.app.aitask.task.execute;

import java.io.File;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.jetty.server.Request;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.utils.FileUtils;
import com.ai.app.aitask.utils.TestJettyServer;
import com.google.gson.Gson;

public class ExecuteTest {

    static final protected Logger  logger    = Logger.getLogger(ExecuteTest.class);

    public static ScheduleDaemon daemon;
    static TestJettyServer       server = null;

    private static String        xml_file;

    @AfterClass
    public static void teardown() throws Exception {
        if (server == null) {
            server.stop();
        }
    }

    @BeforeClass
    public static void start() throws Exception {
        daemon = ScheduleDaemon.instance();
        daemon.start();
        String xml_path = "/com/ai/app/aitask/task/bat/bat_script_task001.xml";
        xml_file = FileUtils.readFileInClasspath(xml_path);
        server = new TestJettyServer(9999) {
            @Override
            public void handle(String u, Request r, HttpServletRequest q, HttpServletResponse p) {
                if ("/fetchTask".equals(u)) {
                    HashMap<String, String> bean = new HashMap<String, String>();
                    bean.put("xml", xml_file);
                    super.handle(new Gson().toJson(bean), r, q, p);
                } else {
                    super.handle(u, r, q, p);
                }
            }
        };
        server.start();
    }

    @Test
    public void testNormal() throws Exception {

        Document doc = DocumentHelper.parseText(xml_file);
        Element root = doc.getRootElement();
        root.element("case").addAttribute(
                "bat_path",
                new File(Thread.currentThread().getContextClassLoader().getResource("").getPath()
                        .toString()).getPath()
                        + "/com/ai/app/aitask/task/bat/bat_run.cmd");
        root.element("case").addAttribute(
                "ini_path",
                new File(Thread.currentThread().getContextClassLoader().getResource("").getPath()
                        .toString()).getPath()
                        + "/com/ai/app/aitask/task/bat/bat_script_task001.ini");

        //        ITaskBuilder ts = TaskDirector.getBatTaskBuilder(root.asXML());
//        ITaskBuilder ts = TaskBuilderFactory.parseBuilder(content).getBuilder(null, Integer.toString(Constants.TASK_TYPE_BAT));
//        Assert.assertTrue(TriggerUnil.waitStateUntil(daemon.getScheduler(), ts.getAuth()
//                .mapKey(), TriggerState.NONE, 1000l));
//        daemon.getScheduler().addTask(ts, true);
//        logger.info(daemon.getScheduler().getTaskState(ts.getAuth().mapKey()));
//        Assert.assertTrue(TriggerUnil.waitStateUntil(daemon.getScheduler(), ts.getAuth()
//                .mapKey(), TriggerState.BLOCKED, 1000l));
//        Assert.assertTrue(TriggerUnil.waitStateUntil(daemon.getScheduler(), ts.getAuth()
//                .mapKey(), TriggerState.NONE, 9000l));
//        Thread.sleep(3000l);
    }
}

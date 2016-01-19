package com.ai.app.aitask.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.jetty.server.Request;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.utils.FileUtils;
import com.ai.app.aitask.utils.TestJettyServer;
import com.ai.app.aitask.utils.TriggerUnil;
import com.google.gson.Gson;

public class ScheduleTester {

    transient final public static Log log = LogFactory.getLog(ScheduleTester.class);

    private static ScheduleDaemon     daemon;
    private static TestJettyServer    server;
    private static String             xml;

    @BeforeClass
    public static void startup() throws Exception {
        String xml_path = "/com/ai/app/aitask/schedule/task_xml_001.xml";
        xml = FileUtils.readXmlFileInClasspath(xml_path);
        server = new TestJettyServer(9999) {
            @Override
            public void handle(String u, Request r, HttpServletRequest q, HttpServletResponse p) {
                if ("/fetchTask".equals(u)) {
                    HashMap<String, String> bean = new HashMap<String, String>();
                    bean.put("xml", xml);
                    super.handle(new Gson().toJson(bean), r, q, p);
                }
            }
        };
        server.start();
    }
    @AfterClass
    public static void teardown() throws Exception {
        if (server == null)
            server.stop();
    }

    @Before
    public void before() throws Exception {
        daemon = ScheduleDaemon.instance();
        daemon.start();
    }

    @After
    public void after() throws Exception {
        daemon.shutdown();
    }

    @Test
    public void testFetchTask() {
        
    }
    /**
     * TODO 这个测试后半段不稳定
     * @throws Exception
     */
    @Test
    public void testReplaceOnBlock() throws Exception {
        // 1.先设置所有的执行时间在两秒后
        Document document = DocumentHelper.parseText(xml);
        Element root = document.getRootElement();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis() + 2000l));
        String t = c.get(Calendar.SECOND) + " " + c.get(Calendar.MINUTE) + " * * * ?";
        Iterator<?> itr = root.elementIterator("task");
        while (itr.hasNext()) {
            Element ele = (Element) itr.next();
            System.out.println(t);
            ele.attribute("cron").setValue(t);
        }
        xml = root.asXML();

        TaskFetcher fetcher = new TaskFetcher(daemon.getTaskSchedule(), 1000l);
        fetcher.fetch();

        String id1 = ((Element) root.elements("task").get(0)).attributeValue("ID");
        String id2 = ((Element) root.elements("task").get(1)).attributeValue("ID");
        TriggerKey key1 = TriggerKey.triggerKey(id1, "AITASK");
        TriggerKey key2 = TriggerKey.triggerKey(id2, "AITASK");
        System.err.println("==================================================");
        // 2.等待任务执行完毕
        TaskSchedule schedule = daemon.getTaskSchedule();
        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key1, TriggerState.NORMAL, 500l));
        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key2, TriggerState.NORMAL, 500l));

        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key1, TriggerState.BLOCKED, 3000l));
        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key2, TriggerState.BLOCKED, 3000l));

        System.out.println(schedule.getTrigerInfo());
        System.err.println("==================================================");
        // 3.重新加载之前的任务
        fetcher.fetch();
        System.out.println(daemon.getTaskSchedule().getTrigerInfo());
        
        Thread.sleep(2000l);// 需要等之前的任务执行完，才会进行替换
        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key1, TriggerState.NORMAL, 3000l));
        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key2, TriggerState.NORMAL, 3000l));

        // 4.查看是否为最新任务
        Assert.assertTrue(TriggerUnil.waitPreviousTimeNull(schedule, key1, 5000l));
        Assert.assertTrue(TriggerUnil.waitPreviousTimeNull(schedule, key2, 5000l));
        Assert.assertEquals(null, schedule.getScheduler().getTrigger(key1).getPreviousFireTime());
        Assert.assertEquals(null, schedule.getScheduler().getTrigger(key2).getPreviousFireTime());

        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key1, TriggerState.NORMAL, 5000l));
        System.out.println(schedule.getTrigerInfo());
    }
}

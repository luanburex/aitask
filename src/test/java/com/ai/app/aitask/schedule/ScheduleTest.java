package com.ai.app.aitask.schedule;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.task.builder.ITaskBuilder;
import com.ai.app.aitask.utils.FileUtils;
import com.ai.app.aitask.utils.TestJettyServer;
import com.ai.app.aitask.utils.TriggerUnil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ScheduleTest {

    final public static Log        log = LogFactory.getLog(ScheduleTest.class);

    private static Gson            gson;
    private static ScheduleDaemon  daemon;
    private static TestJettyServer server;
    private static String          response;

    @BeforeClass
    public static void startup() throws Exception {
        server = new TestJettyServer(9999) {
            @Override
            public void handle(String u, Request r, HttpServletRequest q, HttpServletResponse p) {
                log.info("from:" + r.getRemoteAddr());
                log.info("user:" + r.getRemoteUser());

                String url = Config.instance("client.properties").getProperty(null,
                        "aitask.sync.url");
                log.info("url1:" + url);
                log.info("url2:" + url.replace("http(s?)://(.*):", "http://1.2.3.4:"));
                System.out.println("https?://.*:");
                if ("/fetchTask".equals(u)) {
                    super.handle(response, r, q, p);
                } else if ("/query".equals(u)) {
                    for (Object context : daemon.getScheduler().getTasks()) {
                        JobDataMap jobmap = ((JobExecutionContext) context).getMergedJobDataMap();
                        Map<String, Map<String, Object>> datamap;
                        datamap = Caster.cast(jobmap.get("datamap"));
                        log.info("query:" + datamap);
                    }
                    super.handle(u, r, q, p);
                } else if ("/result".equals(u)) {
                    System.out.println(r.getContentLength());
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                r.getInputStream(), "UTF-8"));
                        for (String line = reader.readLine(); null != line; line = reader
                                .readLine()) {
                            System.out.println("rst:" + line);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    super.handle("done", r, q, p);
                } else {
                    super.handle(u, r, q, p);
                }
            }
        };
        daemon = ScheduleDaemon.instance();
        gson = new GsonBuilder().setPrettyPrinting().create();
        server.start();
    }
    @AfterClass
    public static void teardown() throws Exception {
        if (server == null) {
            server.stop();
        }
    }
    @Before
    public void before() throws Exception {
        daemon.start();
    }
    @After
    public void after() throws Exception {
        daemon.shutdown();
    }
    /**
     * 终端发起-取单个任务
     *
     * @throws Exception
     */
    @Test
    public void testFetchTask() throws Exception {
        String file = FileUtils.readFileInClasspath("task_schedule_001.json");
        JsonObject json = new JsonParser().parse(file).getAsJsonObject();
        response = json.toString();
        //        System.err.println(gson.toJson(json));

        TaskFetcher fetcher = new TaskFetcher();
        List<ITaskBuilder> taskList = fetcher.fetch(null, null);
        for (ITaskBuilder taskBuilder : taskList) {
            Map<String, Object> map = taskBuilder.getContent();
            map = Caster.cast(map.get("datamap"));

            Assert.assertTrue("不包含任务结构", map.containsKey("task"));
            Map<String, Object> taskmap = Caster.cast(map.get("task"));
            Assert.assertTrue("不包含执行计划", taskmap.containsKey("cron"));
            Assert.assertTrue("不包含执任务ID", taskmap.containsKey("taskId"));
            Assert.assertTrue("不包含执计划组织", taskmap.containsKey("taskGroup"));
            //            Assert.assertTrue("不包含执计划策略", taskmap.containsKey("taskCategory"));
            Assert.assertTrue("不包含执计划ID", taskmap.containsKey("planId"));
            Assert.assertTrue("不包含执计划名称", taskmap.containsKey("planName"));

            Assert.assertTrue("不包含任务数据结构", map.containsKey("datalist"));
            List<Map<String, Object>> datalist = Caster.cast(map.get("datalist"));
            Map<String, Object> datamap = datalist.get(0);
            Assert.assertTrue("不包含任务数据ID", datamap.containsKey("dataId"));
            Assert.assertTrue("不包含任务数据标签", datamap.containsKey("tags"));
            Assert.assertTrue("不包含任务数据名称", datamap.containsKey("dataName"));
            Assert.assertTrue("不包含任务脚本ID", datamap.containsKey("scriptId"));
            
            Assert.assertTrue("不包含任务数据细节", map.containsKey("detaillist"));
            String dataId = (String) datamap.get("dataId");
            Map<String, Map<String, Object>> detaillist = Caster.cast(map.get("detaillist"));
        }
        for (ITaskBuilder taskBuilder : taskList) {
            boolean result = daemon.getScheduler().addTask(taskBuilder, true);
        }
        
//        for (List<?> list = daemon.getScheduler().getTasks(); list.size() == 0;) {
//            list = daemon.getScheduler().getTasks();
//            Thread.sleep(50);
//        }
        int counter = 0;
        for (List<Object> list = daemon.getScheduler().getTasks(); list.size() != 0;) {
            if (counter % 5 == 0) {
                for (Object obj : list) {
                    JobExecutionContext context = (JobExecutionContext) obj;
                    String info = context.getTrigger().getKey().toString();
                    System.err.println("exe:[" + list.size() + "]" + info);
                }
            }
            Thread.sleep(100);
            counter++;
            list = daemon.getScheduler().getTasks();
        }
        List<?> list = daemon.getScheduler().getTasks();
        System.err.println("fin1:[" + list.size() + "]");
        Thread.sleep(50000);
        list = daemon.getScheduler().getTasks();
        System.err.println("fin2:[" + list.size() + "]");
    }
    /**
     * 终端发起-任务同步
     */
    public void testTaskSynchronize() {

    }
    /**
     * 终端发起-解析XML任务
     */
    @Test
    public void testFetchXMLTask() {
    }
    /**
     * 终端发起-解析JSON任务
     *
     * @throws Exception
     */
    @Test
    public void testFetchJSONTask() throws Exception {
        String file = FileUtils.readFileInClasspath("task_schedule_001.json");
        JsonObject json = new JsonParser().parse(file).getAsJsonObject();
        JsonObject plan = json.get("plan").getAsJsonArray().get(0).getAsJsonObject();
        Calendar c = Calendar.getInstance();
        String t = c.get(Calendar.SECOND) + " " + c.get(Calendar.MINUTE) + " * * * ?";
        c.setTime(new Date(System.currentTimeMillis() + 2000l));
        t = c.get(Calendar.SECOND) + " " + c.get(Calendar.MINUTE) + " * * * ?";
        plan.addProperty("cron", t);
        response = json.toString();

        TaskFetcher fetcher = new TaskFetcher();
        fetcher.fetch(null, null);
        Thread.sleep(5000l);
    }
    /**
     * 平台发起-发布任务
     */
    @Test
    public void testArrangeTask() {

    }

    /**
     * TODO 这个测试后半段不稳定
     *
     * @throws Exception
     */
    @Test
    public void testReplaceOnBlock() throws Exception {
        // 1.先设置所有的执行时间在两秒后
        String file = FileUtils.readFileInClasspath("/com/ai/app/aitask/schedule/task_xml_001.xml");
        Document xml = DocumentHelper.parseText(file);
        Element root = xml.getRootElement();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis() + 2000l));
        String t = c.get(Calendar.SECOND) + " " + c.get(Calendar.MINUTE) + " * * * ?";
        log.info("target time : " + t);
        System.err.println(t);
        Iterator<?> itr = root.elementIterator("task");
        while (itr.hasNext()) {
            Element ele = (Element) itr.next();
            ele.attribute("cron").setValue(t);
        }
        HashMap<String, String> bean = new HashMap<String, String>();
        bean.put("xml", root.asXML());
        response = new Gson().toJson(bean);

        TaskFetcher fetcher = new TaskFetcher();
        fetcher.fetch(null, null);

        String id1 = ((Element) root.elements("task").get(0)).attributeValue("ID");
        String id2 = ((Element) root.elements("task").get(1)).attributeValue("ID");
        TriggerKey key1 = TriggerKey.triggerKey(id1, "AITASK");
        TriggerKey key2 = TriggerKey.triggerKey(id2, "AITASK");
        System.err.println("==================================================");
        // 2.等待任务执行完毕
        ITaskScheduler schedule = daemon.getScheduler();
        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key1, TriggerState.NORMAL, 500l));
        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key2, TriggerState.NORMAL, 500l));

        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key1, TriggerState.BLOCKED, 3000l));
        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key2, TriggerState.BLOCKED, 3000l));

        log.info("s2 trigger info : " + schedule.getInfo());
        System.err.println("==================================================");
        // 3.重新加载之前的任务
        fetcher.fetch(null, null);
        log.info("s3 trigger info : " + schedule.getInfo());

        Thread.sleep(2000l);// 需要等之前的任务执行完，才会进行替换
        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key1, TriggerState.NORMAL, 3000l));
        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key2, TriggerState.NORMAL, 3000l));

        // 4.查看是否为最新任务
        Assert.assertTrue(TriggerUnil.waitPreviousTimeNull(schedule, key1, 5000l));
        Assert.assertTrue(TriggerUnil.waitPreviousTimeNull(schedule, key2, 5000l));
        Trigger trigger1 = (Trigger) schedule.getTrigger(key1);
        Trigger trigger2 = (Trigger) schedule.getTrigger(key2);
        Assert.assertEquals(null, trigger1.getPreviousFireTime());
        Assert.assertEquals(null, trigger2.getPreviousFireTime());

        Assert.assertTrue(TriggerUnil.waitStateUntil(schedule, key1, TriggerState.NORMAL, 5000l));
        log.info("s4 trigger info : " + schedule.getInfo());
    }
}

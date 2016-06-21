package com.ai.app.aitask.task.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.jetty.server.Request;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ai.app.aitask.utils.TestJettyServer;

public class FetcherTest {

    static final protected Logger       logger                 = Logger.getLogger(FetcherTest.class);

    Mockery                             mock_context        = new Mockery();
    static TestJettyServer              server              = null;

    protected final JobExecutionContext job_execute_context = mock_context
            .mock(JobExecutionContext.class);

    @BeforeClass
    public static void startup() throws Exception {
        server = new TestJettyServer(9999) {
            @Override
            public void handle(String u, Request r, HttpServletRequest q, HttpServletResponse p) {
                if ("/fetchTask".equals(u)) {
                } else {
                    super.handle(u, r, q, p);
                }
            }
        };
        server.start();
    }
    @AfterClass
    public static void teardown() throws Exception {
        if (server == null) {
            server.stop();
        }
    }

    @Before
    public void before() {
        final JobDataMap job_data_map = new JobDataMap();
        mock_context.checking(new Expectations() {
            {
                allowing(job_execute_context).getMergedJobDataMap();
                will(returnValue(job_data_map));
            }
        });
    }

    @Test
    public void testNormal() throws JobExecutionException, DocumentException {

        String ini_result_file = Thread.currentThread().getContextClassLoader().getResource("")
                .getPath().toString()
                + "/com/ai/app/aitask/task/parts/iniResult_001.ini";

        JobExecutionContext context = job_execute_context;

        context.getMergedJobDataMap().put("task_id", "124");
        context.getMergedJobDataMap().put("case_id", "125");
        context.getMergedJobDataMap().put("case_name", "测试用例");
        context.getMergedJobDataMap().put("case_category", "0");
        context.getMergedJobDataMap().put("case_module", "测试");
        context.getMergedJobDataMap().put("group_no", "111");
        context.getMergedJobDataMap().put("group_name", "全国");
        context.getMergedJobDataMap().put("agent_id", "1");
        context.getMergedJobDataMap().put("agent_name", "测试主机");

//        List<ExecutorProbe> probes = new ArrayList<ExecutorProbe>();
//        probes.add(new ExecutorProbe("1", "登录", 1, 1000, 4000, 8000, ""));
//        probes.add(new ExecutorProbe("2", "账单查询,打开菜单", 2, 1000, 4000, 8000, ""));
//        probes.add(new ExecutorProbe("3", "账单查询,业务查询", 3, 1000, 4000, 8000, ""));
//        probes.add(new ExecutorProbe("4", "注销", 3, 1000, 4000, 8000, ""));
//        context.getMergedJobDataMap().put("probes", probes);
//
//        // context.getMergedJobDataMap().put("ini_path", ini_result_file);
//        IniResultFetcher fetcher = new IniResultFetcher(ini_result_file);
//        String result_xml = fetcher.fetch(context);

//        logger.info(result_xml);

        org.dom4j.Document document = DocumentHelper.parseText(null);
        Element root = document.getRootElement();
        Assert.assertEquals("ResultCase", root.getName());
        Assert.assertEquals(root.attributeValue("run_id"), "124");
        Assert.assertEquals(root.attributeValue("task_id"), "124");
        Assert.assertEquals(root.attributeValue("group_name"), "全国");
        Assert.assertEquals(root.attributeValue("agent_name"), "测试主机");
        Assert.assertEquals(root.attributeValue("rst_log"), "<ele>skjdglsjdg</ele>\"aaa\"");
        Assert.assertEquals("2013/06/03 21:54:50", root.attributeValue("start_time"));
        Assert.assertEquals("2013/06/03 21:55:50", root.attributeValue("end_time"));
        Assert.assertEquals("1", root.attributeValue("result"));
        Assert.assertEquals("28884", root.attributeValue("eclapse"));
        Assert.assertEquals("", root.attributeValue("key_eclapse"));

        Assert.assertEquals(4, root.element("steps").elements().size());
        Assert.assertEquals("1",
                root.element("steps").element("ResultStep").attributeValue("step_id"));
        Assert.assertEquals("登录",
                root.element("steps").element("ResultStep").attributeValue("step_name"));
        Assert.assertEquals("0",
                root.element("steps").element("ResultStep").attributeValue("result"));
        Assert.assertEquals("3549",
                root.element("steps").element("ResultStep").attributeValue("eclapse"));

    }

    @Test
    public void testNotProbesNormal() throws JobExecutionException, DocumentException {

        String ini_result_file = Thread.currentThread().getContextClassLoader().getResource("")
                .getPath().toString()
                + "/com/ai/app/aitask/task/parts/iniResult_001.ini";

        JobExecutionContext context = job_execute_context;

        context.getMergedJobDataMap().put("task_id", "124");
        context.getMergedJobDataMap().put("case_id", "125");
        context.getMergedJobDataMap().put("case_name", "测试用例");
        context.getMergedJobDataMap().put("case_category", "0");
        context.getMergedJobDataMap().put("case_module", "测试");
        context.getMergedJobDataMap().put("group_no", "111");
        context.getMergedJobDataMap().put("group_name", "全国");
        context.getMergedJobDataMap().put("agent_id", "1");
        context.getMergedJobDataMap().put("agent_name", "测试主机");

        // context.getMergedJobDataMap().put("ini_path", ini_result_file);
//        IniResultFetcher fetcher = new IniResultFetcher(ini_result_file);
//        String result_xml = fetcher.fetch(context);
//
//        logger.info(result_xml);
//
//        org.dom4j.Document document = DocumentHelper.parseText(result_xml);
//        Element root = document.getRootElement();
//        Assert.assertEquals("ResultCase", root.getName());
//        Assert.assertEquals(root.attributeValue("run_id"), "124");
//        Assert.assertEquals(root.attributeValue("task_id"), "124");
//        Assert.assertEquals(root.attributeValue("group_name"), "全国");
//        Assert.assertEquals(root.attributeValue("agent_name"), "测试主机");
//        Assert.assertEquals(root.attributeValue("rst_log"), "<ele>skjdglsjdg</ele>\"aaa\"");
//        Assert.assertEquals("2013/06/03 21:54:50", root.attributeValue("start_time"));
//        Assert.assertEquals("2013/06/03 21:55:50", root.attributeValue("end_time"));
//        Assert.assertEquals("1", root.attributeValue("result"));
//        Assert.assertEquals("28884", root.attributeValue("eclapse"));
//        Assert.assertEquals("", root.attributeValue("key_eclapse"));
//
//        Assert.assertEquals(null, root.element("steps"));

    }

    @Test
    public void testError() throws JobExecutionException, DocumentException {

        String ini_result_file = Thread.currentThread().getContextClassLoader().getResource("")
                .getPath().toString()
                + "/com/ai/app/aitask/task/parts/iniResult_001.ini";

        JobExecutionContext context = job_execute_context;

        context.getMergedJobDataMap().put("task_id", "124");
        context.getMergedJobDataMap().put("case_id", "125");
        context.getMergedJobDataMap().put("case_name", "测试用例");
        context.getMergedJobDataMap().put("case_category", "0");
        context.getMergedJobDataMap().put("case_module", "测试");
        context.getMergedJobDataMap().put("group_no", "111");
        context.getMergedJobDataMap().put("group_name", "全国");
        context.getMergedJobDataMap().put("agent_id", "1");
        context.getMergedJobDataMap().put("agent_name", "测试主机");

        // context.getMergedJobDataMap().put("ini_path", ini_result_file);
//        IniResultFetcher fetcher = new IniResultFetcher(ini_result_file);
//        String result_xml = fetcher.fetch(context);
//
//        logger.info(result_xml);

//        org.dom4j.Document document = DocumentHelper.parseText(result_xml);
//        Element root = document.getRootElement();
//        Assert.assertEquals("ResultCase", root.getName());
//        Assert.assertEquals(root.attributeValue("run_id"), "124");
//        Assert.assertEquals(root.attributeValue("task_id"), "124");
//        Assert.assertEquals(root.attributeValue("group_name"), "全国");
//        Assert.assertEquals(root.attributeValue("agent_name"), "测试主机");
//        Assert.assertEquals(root.attributeValue("rst_log"), "<ele>skjdglsjdg</ele>\"aaa\"");
//        Assert.assertEquals("2013/06/03 21:54:50", root.attributeValue("start_time"));
//        Assert.assertEquals("2013/06/03 21:55:50", root.attributeValue("end_time"));
//        Assert.assertEquals("1", root.attributeValue("result"));
//        Assert.assertEquals("28884", root.attributeValue("eclapse"));
//        Assert.assertEquals("", root.attributeValue("key_eclapse"));
//
//        Assert.assertEquals(null, root.element("steps"));

    }
}

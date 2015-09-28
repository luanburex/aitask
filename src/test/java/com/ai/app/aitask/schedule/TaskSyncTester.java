package com.ai.app.aitask.schedule;

import java.util.Date;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.utils.FileReaderUtils;
import com.ai.app.aitask.utils.TestJettyServer;
import com.ai.app.aitask.utils.TriggerStateWaitUnil;


public class TaskSyncTester {

	transient final public static Log log = LogFactory.getLog(TaskSyncTester.class);
	
	static TestJettyServer server = null;
	public static ScheduleDaemon sd;
	
	@BeforeClass
	public static void startup() throws Exception{
		String xml_file = Thread.currentThread().getContextClassLoader().getResource("").getPath().toString() 
				+ "/com/ai/app/aitask/schedule/task_xml_001.xml";
		String xml_str = FileReaderUtils.readFileToString(xml_file);
		server = new TestJettyServer(xml_str);
		server.start();
	}
	@AfterClass
	public static void teardown() throws Exception{
		if (server == null)
			server.stop();
	}
	
	@Before
	public void before() throws Exception{
		sd = ScheduleDaemon.instance();
		sd.start();
	}
	
	@After
	public void after() throws Exception{
		sd.shutdown();
	}
	
	/*
	@Test
	public void testNormal() throws Exception{
		TaskFetch tf = new TaskFetch(sd.getTaskSchedule());
		tf.fetch();
		log.debug(sd.getTaskSchedule().getTaskState(TriggerKey.triggerKey("11230", "AITASK")));
		log.debug(sd.getTaskSchedule().getTaskState(TriggerKey.triggerKey("11231", "AITASK")));
		Assert.assertEquals(TriggerState.NORMAL, sd.getTaskSchedule().getTaskState(TriggerKey.triggerKey("11230", "AITASK")));
		Assert.assertEquals(TriggerState.NORMAL, sd.getTaskSchedule().getTaskState(TriggerKey.triggerKey("11231", "AITASK")));
	}
	@Test
	public void testReplace() throws Exception{
		String xml = server.getTaskXmlStr();
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		
		
		//1.先设置所有的执行时间在两秒后
		Date next_second = new Date(System.currentTimeMillis() + 2000l);
		Iterator<Element> itr = root.elementIterator("task");
		while(itr.hasNext()){
			Element ele = itr.next();
			System.out.println(next_second.getSeconds() + " " + next_second.getMinutes() + " * * * ?");
			ele.attribute("cron").setValue(next_second.getSeconds() + " " + next_second.getMinutes() + " * * * ?");
			ele.setAttributeValue("cron", next_second.getSeconds() + " " + next_second.getMinutes() + " * * * ?");
		}
		server.setTaskXmlStr(root.asXML());
		TaskFetch tf = new TaskFetch(sd.getTaskSchedule());
		tf.fetch();
		
		TriggerKey key1 = TriggerKey.triggerKey("11230", "AITASK");
		TriggerKey key2 = TriggerKey.triggerKey("11230", "AITASK");
		
		//2.等到任务开始执行
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				key1, TriggerState.BLOCKED, 3000l));
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				key2, TriggerState.BLOCKED, 3000l));
		
		
		System.out.println(sd.getTaskSchedule().getTrigerInfo());
		
		//3.重新加载之前的任务
		next_second = new Date(System.currentTimeMillis() + 5*60*1000l);
		itr = root.elementIterator("task");
		while(itr.hasNext()){
			Element ele = itr.next();
			System.out.println(next_second.getSeconds() + " " + next_second.getMinutes() + " * * * ?");
			ele.attribute("cron").setValue(next_second.getSeconds() + " " + next_second.getMinutes() + " * * * ?");
			ele.setAttributeValue("cron", next_second.getSeconds() + " " + next_second.getMinutes() + " * * * ?");
		}
		server.setTaskXmlStr(root.asXML());
		tf.fetch();
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				key1, TriggerState.BLOCKED, 3000l));
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				key2, TriggerState.BLOCKED, 3000l));
		System.out.println(sd.getTaskSchedule().getTrigerInfo());
		
		//4.查看是否为最新任务
		Assert.assertEquals(null, sd.getTaskSchedule().getScheduler().getTrigger(key1).getPreviousFireTime());
		Assert.assertEquals(null, sd.getTaskSchedule().getScheduler().getTrigger(key2).getPreviousFireTime());
		
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				key1, TriggerState.NORMAL, 5000l));
		System.out.println(sd.getTaskSchedule().getTrigerInfo());
	}
	*/
	@Test
	public void testReplaceOnBlock() throws Exception{
		String xml = server.getTaskXmlStr();
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		
		
		//1.先设置所有的执行时间在两秒后
		Date next_second = new Date(System.currentTimeMillis() + 2000l);
		Iterator<Element> itr = root.elementIterator("task");
		while(itr.hasNext()){
			Element ele = itr.next();
			System.out.println(next_second.getSeconds() + " " + next_second.getMinutes() + " * * * ?");
			ele.attribute("cron").setValue(next_second.getSeconds() + " " + next_second.getMinutes() + " * * * ?");
			ele.setAttributeValue("cron", next_second.getSeconds() + " " + next_second.getMinutes() + " * * * ?");
		}
		server.setTaskXmlStr(root.asXML());
		TaskFetch tf = new TaskFetch(sd.getTaskSchedule(), 1000l);
		tf.fetch();
		
		TriggerKey key1 = TriggerKey.triggerKey("11230", "AITASK");
		TriggerKey key2 = TriggerKey.triggerKey("11230", "AITASK");
		
		//2.等到任务开始执行
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				key1, TriggerState.BLOCKED, 3000l));
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				key2, TriggerState.BLOCKED, 3000l));
		
		
		System.out.println(sd.getTaskSchedule().getTrigerInfo());
		
		//3.重新加载之前的任务
		
		server.setTaskXmlStr(root.asXML());
		tf.fetch();
	
		System.out.println(sd.getTaskSchedule().getTrigerInfo());
		Thread.sleep(2000l);//需要等之前的任务执行完，才会进行替换
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				key1, TriggerState.NORMAL, 3000l));
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				key2, TriggerState.NORMAL, 3000l));
		
		
		
		//4.查看是否为最新任务
		Assert.assertTrue(TriggerStateWaitUnil.waitPreviousTimeNull(sd.getTaskSchedule(), key1, 5000l));
		Assert.assertTrue(TriggerStateWaitUnil.waitPreviousTimeNull(sd.getTaskSchedule(), key2, 5000l));
		Assert.assertEquals(null, sd.getTaskSchedule().getScheduler().getTrigger(key1).getPreviousFireTime());
		Assert.assertEquals(null, sd.getTaskSchedule().getScheduler().getTrigger(key2).getPreviousFireTime());
		
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				key1, TriggerState.NORMAL, 5000l));
		System.out.println(sd.getTaskSchedule().getTrigerInfo());
	}
	
}

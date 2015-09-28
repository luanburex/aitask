package com.ai.app.aitask.task.bat;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.quartz.Trigger.TriggerState;

import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.task.TaskDirector;
import com.ai.app.aitask.task.tasks.ITaskBuilder;
import com.ai.app.aitask.utils.FileReaderUtils;
import com.ai.app.aitask.utils.TestJettyServer;
import com.ai.app.aitask.utils.TriggerStateWaitUnil;

public class BatTaskTest {

	final static private Logger log = Logger.getLogger(BatTaskTest.class);

	public static ScheduleDaemon sd;
	static TestJettyServer server = null;
	
	@AfterClass
	public static void teardown() throws Exception{
		if (server == null)
			server.stop();
	}
	
	@BeforeClass
	public static void start() throws Exception{
		sd = ScheduleDaemon.instance();
		server = new TestJettyServer(null);
		server.start();
	}
	
	@Test
	public void testNormal() throws Exception{
		String xml_file = Thread.currentThread().getContextClassLoader().getResource("").getPath().toString() 
				+ "/com/ai/app/aitask/task/bat/bat_script_task001.xml";
		String xml_str = FileReaderUtils.readFileToString(xml_file);
		
		Document doc = DocumentHelper.parseText(xml_str);
        Element root = doc.getRootElement();
        root.element("case").setAttributeValue("bat_path", new File(Thread.currentThread().getContextClassLoader().getResource("").getPath().toString()).getPath() 
				+ "/com/ai/app/aitask/task/bat/bat_run.cmd");
        root.element("case").setAttributeValue("ini_path", new File(Thread.currentThread().getContextClassLoader().getResource("").getPath().toString()).getPath() 
				+ "/com/ai/app/aitask/task/bat/bat_script_task001.ini");
        
		
		ITaskBuilder ts = TaskDirector.getBatTaskBuilder(root.asXML());
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				ts.getTrigger().getKey(), TriggerState.NONE, 1000l));
		sd.getTaskSchedule().addTask(ts, true);
		log.info(sd.getTaskSchedule().getTaskState(ts.getTrigger().getKey()));
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				ts.getTrigger().getKey(), TriggerState.BLOCKED, 1000l));
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				ts.getTrigger().getKey(), TriggerState.NONE, 9000l));
		Thread.sleep(3000l);
	}
}

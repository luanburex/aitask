package com.ai.app.aitask.task;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.quartz.Trigger.TriggerState;

import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.task.tasks.ITaskBuilder;
import com.ai.app.aitask.task.tasks.TaskDirector;
import com.ai.app.aitask.utils.FileReaderUtils;
import com.ai.app.aitask.utils.TriggerStateWaitUnil;

public class SerialTaskTimeoutTest {
	final static private Logger log = Logger.getLogger(SerialTaskTest.class);

	public static ScheduleDaemon sd;
	
	@BeforeClass
	public static void start() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SchedulerException, IOException, SQLException{
		sd = ScheduleDaemon.instance();
	}

	@Test
	public void testTimeout() throws Exception, IllegalAccessException, ClassNotFoundException, SchedulerException, IOException, SQLException{
		ScheduleDaemon sd = ScheduleDaemon.instance();
		String xml_file = Thread.currentThread().getContextClassLoader().getResource("").getPath().toString() 
				+ "/com/ai/app/aitask/task/cmd_script_task004_timeout.xml";
		String xml_str = FileReaderUtils.readFileToString(xml_file);
		ITaskBuilder ts = TaskDirector.getCmdTaskBuilder(xml_str);
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				ts.getTrigger().getKey(), TriggerState.NONE, 1000l));
		sd.getTaskSchedule().addTask(ts, true);
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				ts.getTrigger().getKey(), TriggerState.BLOCKED, 1000l));
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				ts.getTrigger().getKey(), TriggerState.NONE, 1000l));
	}
}

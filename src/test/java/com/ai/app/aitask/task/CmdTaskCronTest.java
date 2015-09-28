package com.ai.app.aitask.task;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.quartz.Trigger.TriggerState;

import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.task.tasks.ITaskBuilder;
import com.ai.app.aitask.utils.FileReaderUtils;
import com.ai.app.aitask.utils.TriggerStateWaitUnil;

public class CmdTaskCronTest {
	final static private Logger log = Logger.getLogger(CmdTaskCronTest.class);

	public static ScheduleDaemon sd;
	
	@BeforeClass
	public static void start() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SchedulerException, IOException, SQLException{
		sd = ScheduleDaemon.instance();
	}
	
	@Test
	public void testCronRun() throws Exception{
		String xml_file = Thread.currentThread().getContextClassLoader().getResource("").getPath().toString() 
				+ "/com/ai/app/aitask/task/cmd_script_task005_cron.xml";
		String xml_str = FileReaderUtils.readFileToString(xml_file);
		ITaskBuilder ts = TaskDirector.getCmdTaskBuilder(xml_str);
		Date next_second = new Date(System.currentTimeMillis() + 2000l);
		log.info(next_second);
		log.info(next_second.getSeconds() + " " + next_second.getMinutes() + " * * * ?");
		ts.getDatamap().put("cron", next_second.getSeconds() + " " + next_second.getMinutes() + " * * * ?");
		ts.generate();
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				ts.getTrigger().getKey(), TriggerState.NONE, 1000l));
		
		sd.getTaskSchedule().addTask(ts, true);
		
		log.info(sd.getTaskSchedule().getTaskState(ts.getTrigger().getKey()));
		
		log.info(sd.getTaskSchedule().getScheduler().getTrigger(ts.getTrigger().getKey()).getPreviousFireTime());
		log.info(sd.getTaskSchedule().getScheduler().getTrigger(ts.getTrigger().getKey()).getNextFireTime());
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				ts.getTrigger().getKey(), TriggerState.NORMAL, 2000l));
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				ts.getTrigger().getKey(), TriggerState.BLOCKED, 5000l));
		log.info(sd.getTaskSchedule().getTaskState(ts.getTrigger().getKey()));
		log.info(sd.getTaskSchedule().getScheduler().getTrigger(ts.getTrigger().getKey()).getPreviousFireTime());
		log.info(sd.getTaskSchedule().getScheduler().getTrigger(ts.getTrigger().getKey()).getNextFireTime());
		Assert.assertTrue(TriggerStateWaitUnil.waitStateUntil(sd.getTaskSchedule(), 
				ts.getTrigger().getKey(), TriggerState.NORMAL, 5000l));

	}
}

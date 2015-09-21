package com.ai.app.aitask.task.factory;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.ai.app.aitask.task.parts.impl.CmdTaskExecutor;
import com.ai.app.aitask.task.tasks.ITask;
import com.ai.app.aitask.task.tasks.impl.SerialTask;

public class TaskFactory {

	final public static String TASK_GROUP = "AI";
	final public static String SERIAL_CMD_TASK="Serial_CMD_Task";
	
	
	public static ITask createSerialCmdTask(String xml){
		
//		String task_id = "aaa";
//		
//		SerialTask task = new SerialTask();
//		
//		JobKey jobKey = new JobKey(SERIAL_CMD_TASK, TASK_GROUP);
//		JobDetail job = JobBuilder.newJob(SerialTask.class).withIdentity(jobKey).storeDurably().build();
//		task.setJobDetail(job);
//		
//		JobDataMap datamap = new JobDataMap();
//		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(
//				task_id, TASK_GROUP).startNow()
//				.withSchedule(SimpleScheduleBuilder.simpleSchedule())
//				.usingJobData(datamap)
//				.build();
//		task.setTrigger(trigger);
		
//		String cmd = "ping -n 3 127.0.0.1";
//		task.setExecutor(new CmdTaskExecutor(cmd));
//		System.out.println(task.getExecutor());
//		
//		return task;
		return null;
	}
}

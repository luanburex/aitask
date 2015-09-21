package com.ai.app.aitask.schedule;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

import com.ai.app.aitask.listener.OpenAndCloseListner;
import com.ai.app.aitask.listener.TaskTimeoutListener;
import com.ai.app.aitask.task.tasks.ITaskBuilder;


public class TaskSchedule {

	private final static transient Logger log = Logger
			.getLogger(TaskSchedule.class);

	/**
	 * the base scheduler of Quartz
	 */
	private Scheduler scheduler;
	
	
	public Scheduler getScheduler(){
		return this.scheduler;
	}
	
	
	/**
	 * the Constructor of the taskschedule:get properties and set listeners.
	 * 
	 * @param scheduler
	 * @throws IOException
	 * @throws SchedulerException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public TaskSchedule(Scheduler scheduler) throws IOException,
			SchedulerException, SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		
		log.debug("["+scheduler.getSchedulerName()+"]"+"set the Scheduler.");
		this.scheduler = scheduler;
		
		log.debug("["+scheduler.getSchedulerName()+"]"+"add the Scheduler listener.");
		scheduler.getListenerManager().addSchedulerListener(new OpenAndCloseListner());

		
		log.debug("["+scheduler.getSchedulerName()+"]"+"add listeners to all task.");
		
//		scheduler.getListenerManager().addJobListener(new BeforeAndAfterTaskListener(),
//				EverythingMatcher.allJobs());
		scheduler.getListenerManager().addJobListener(new TaskTimeoutListener());
		scheduler.start();
	}
	
	
	/**
	 * add task to schduler
	 * 
	 * @param task
	 * @throws SchedulerException
	 * @throws ParseException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws SQLException
	 */
	public void addTask(ITaskBuilder taskBuilder, boolean replace)
			throws SchedulerException, ParseException,
			IllegalArgumentException, IllegalAccessException, SQLException,
			InstantiationException, ClassNotFoundException, IOException {

		JobDetail job = taskBuilder.getJobDetail();
		Trigger trigger = taskBuilder.getTrigger();

		log.info("["+this.scheduler.getSchedulerName()+"]"+"add the task [" + trigger.getKey().toString() + "]");

		Map<JobDetail, Set<? extends Trigger>> tasks = new HashMap<JobDetail, Set<? extends Trigger>>();
		Set<Trigger> triggerlist = new HashSet<Trigger>();
		triggerlist.add(trigger);
		tasks.put(job, triggerlist);
		scheduler.scheduleJobs(tasks, replace);
		
		log.debug("["+this.scheduler.getSchedulerName()+"]there is " +   scheduler.getCurrentlyExecutingJobs().size()
				+ " jobs running.");
		
		
		log.info("["+this.scheduler.getSchedulerName()+"]the trigger [" +trigger.getKey() + "] " 
				+ "will start at" + trigger.getStartTime() + " and trigger state is " 
				+ this.scheduler.getTriggerState(trigger.getKey()).toString());

	}
	public TriggerState getTaskStateByTrigger(TriggerKey triggerKey) throws SchedulerException{
		return scheduler.getTriggerState(triggerKey);
	}
	
	public TriggerState getTaskState(TriggerKey triggerKey)
			throws SchedulerException {
		return this.getTaskStateByTrigger(triggerKey);
	}
	
	public void interruptByJobKey(TriggerKey triggerKey) throws SchedulerException{
		
		JobKey jobKey = this.getScheduler().getTrigger(triggerKey).getJobKey();
		scheduler.interrupt(jobKey);
	}
	
}

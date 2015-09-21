package com.ai.app.aitask.listener;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;



public class TaskTimeoutListener implements JobListener{

	private final static transient Logger log = Logger
	.getLogger(TaskTimeoutListener.class);
	private TimeOutThread _timeoutthread;
	
	
	public String getName() {
		return "TaskTimeoutListener";
	}

	public void jobExecutionVetoed(JobExecutionContext context) {
		log.debug("[" + context.getTrigger().getKey().toString() + "]" + "the timeout listener is running for "+context.getJobDetail().getKey().getName());
	}

	public void jobToBeExecuted(JobExecutionContext context) {
		_timeoutthread = new TimeOutThread(context);
		_timeoutthread.start();
		log.debug("[" + context.getTrigger().getKey().toString() + "]" + "the timeout listener is running for "+context.getJobDetail().getKey().getName());
		
	}

	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException arg1) {
		log.debug("["+context.getTrigger().getKey().toString()+"]"+"the timeout linstener is ended.");
		if(_timeoutthread != null)
			_timeoutthread.interrupt();
	}

}

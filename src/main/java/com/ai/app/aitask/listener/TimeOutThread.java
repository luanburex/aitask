package com.ai.app.aitask.listener;

import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.UnableToInterruptJobException;


/**
 * the thread is for checking whether the job is time out. when the job is time
 * out, run the interrupt of the job to stop the running job.
 * 
 * @author renzq
 * 
 */
public class TimeOutThread extends Thread {

	private final static transient Logger log = Logger
			.getLogger(TimeOutThread.class);
	/**
	 * the default overtime is 30 minutes
	 */
	private static long DEFAULT_TIMEOUT = 1000*60*30l;

	private JobExecutionContext context;
	/**
	 * the overtime
	 */
	private long timeout = -1l;
	
	/**
	 * the end time
	 */
	private long end_time_long = -1l;

	private TimeOutThread() {
		super();
	}

	public TimeOutThread(JobExecutionContext context) {
		this();
		this.context = context;
		if (context != null) {
			
			log.debug(context.getTrigger().getJobDataMap().containsKey("timeout"));
			log.debug(context.getTrigger().getJobDataMap().getLong("timeout"));
			long _temp = (context.getTrigger().getJobDataMap().containsKey("timeout")) ? 
					context.getTrigger().getJobDataMap().getLong("timeout") : DEFAULT_TIMEOUT;
			this.timeout = (_temp > 0l || _temp == -1l) ? _temp : DEFAULT_TIMEOUT;
			log.debug("["+context.getTrigger().getKey()+"]"+"TimeOutThread is create.timeout: " + this.timeout);
			
			this.end_time_long = (context.getTrigger().getJobDataMap().containsKey("endtime")) ? context.getTrigger().getJobDataMap().getLong("endtime") : -1l;
			
			if (end_time_long != -1l){
				long end_time_timeout = end_time_long - System.currentTimeMillis();
				log.debug("["+context.getTrigger().getKey()+"]"+"The end time's overtime is create.timeout: " + end_time_timeout);

				if (end_time_timeout < 0l)
					this.timeout = 0l;
				else if (this.timeout == -1l)
					this.timeout = end_time_timeout;
				else
					this.timeout = end_time_timeout < this.timeout ? end_time_timeout : this.timeout;
				log.debug("["+context.getTrigger().getKey()+"]"+"TimeOutThread is create.timeout: " + this.timeout);

			}
			
		} else {
			log.warn("can't get the Job Execution Context.");
		}
	}

	public void run() {
		if (timeout == -1l)
			return;
		try {
			sleep(this.timeout);
		} catch (InterruptedException ire) {
			return;
		}
		try {
			((InterruptableJob) context.getJobInstance()).interrupt();
			log.info("["+context.getTrigger().getKey().toString()+"]"+"the task timeout thread is interrupted");
		} catch (UnableToInterruptJobException e) {
			log.error("["+context.getTrigger().getKey().toString()+"]" + "Time out interrupt error:", e);
			throw new RuntimeException("exception", e);
		}
	}
}
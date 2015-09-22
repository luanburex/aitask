package com.ai.app.aitask.task.tasks;

import org.quartz.InterruptableJob;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.quartz.UnableToInterruptJobException;

/**
 * 任务接口
 * @author renzq
 *
 */
public interface ITask extends InterruptableJob{
	
	public void before(JobExecutionContext context);
	
	public void after(JobExecutionContext context, JobExecutionException error);

	public void interrupt() throws UnableToInterruptJobException;

	public void execute(JobExecutionContext context) throws JobExecutionException;
	
}
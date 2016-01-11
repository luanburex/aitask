package com.ai.app.aitask.task.builder;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

/**
 * 任务接口
 * @author renzq
 *
 */
public interface ITask extends InterruptableJob{
	
	public void before(JobExecutionContext context);
	
	public void after(JobExecutionContext context, JobExecutionException error);

	@Override
    public void interrupt() throws UnableToInterruptJobException;

	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException;
	
}
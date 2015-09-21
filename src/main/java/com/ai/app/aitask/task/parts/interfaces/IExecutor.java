package com.ai.app.aitask.task.parts.interfaces;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

public interface IExecutor {

	
	public int run(JobExecutionContext context) throws JobExecutionException;
	public void interupt() throws UnableToInterruptJobException;
}

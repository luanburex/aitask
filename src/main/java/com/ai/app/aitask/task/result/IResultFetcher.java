package com.ai.app.aitask.task.result;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public interface IResultFetcher {

	public String fetch(JobExecutionContext context) throws JobExecutionException;
	
	public String error(JobExecutionContext context, JobExecutionException exception) throws JobExecutionException;
}

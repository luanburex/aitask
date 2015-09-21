package com.ai.app.aitask.task.parts.interfaces;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public interface IResultFetcher {

	public String fetch(JobExecutionContext context) throws JobExecutionException;
}

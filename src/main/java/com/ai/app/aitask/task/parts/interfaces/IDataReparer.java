package com.ai.app.aitask.task.parts.interfaces;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public interface IDataReparer {

	public void parepare(JobExecutionContext context) throws JobExecutionException;
}

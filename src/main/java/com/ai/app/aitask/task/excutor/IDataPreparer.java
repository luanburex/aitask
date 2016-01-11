package com.ai.app.aitask.task.excutor;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public interface IDataPreparer {
	public void prepare(JobExecutionContext context) throws JobExecutionException;
}

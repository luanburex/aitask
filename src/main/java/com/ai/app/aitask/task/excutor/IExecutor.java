package com.ai.app.aitask.task.excutor;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

public interface IExecutor {
    public int run(JobExecutionContext context) throws JobExecutionException;
    public void destroy() throws UnableToInterruptJobException;
}

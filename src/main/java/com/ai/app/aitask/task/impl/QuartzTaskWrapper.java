package com.ai.app.aitask.task.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;

import com.ai.app.aitask.task.AbstractTask;
import com.ai.app.aitask.task.ITask;

/**
 * @author Alex Xu
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class QuartzTaskWrapper implements InterruptableJob {
    protected static final Logger logger = Logger.getLogger(QuartzTaskWrapper.class);
    private ITask                 wrapped;

    public QuartzTaskWrapper() {
        this.wrapped = new AbstractTask();
    }

    public final ITask getWrapped() {
        return wrapped;
    }
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Map<String, Object> triggerMap = context.getTrigger().getJobDataMap();
        Map<String, Object> detailMap = context.getJobDetail().getJobDataMap();
        logger.debug("wrapper before execute");
        wrapped.before(triggerMap, detailMap);
        logger.debug("wrapper trying execute");
        wrapped.execute(triggerMap, detailMap);
        logger.debug("wrapper after execute");
        wrapped.after(triggerMap, detailMap);
    }
    @Override
    public void interrupt() throws UnableToInterruptJobException {
        logger.debug("interrupted");
        wrapped.interrupt();
    }
}

package com.ai.app.aitask.task.wrapper;

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
    protected final static Logger log = Logger.getLogger(QuartzTaskWrapper.class);
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
        log.debug("wrapper before execute");
        wrapped.before(triggerMap, detailMap);
        log.debug("wrapper trying execute");
        wrapped.execute(triggerMap, detailMap);
        log.debug("wrapper after execute");
        wrapped.after(triggerMap, detailMap);
    }
    @Override
    public void interrupt() throws UnableToInterruptJobException {
        log.debug("interrupted");
        wrapped.interrupt();
    }
}

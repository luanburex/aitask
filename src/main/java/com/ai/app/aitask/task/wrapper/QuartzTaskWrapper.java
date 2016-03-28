package com.ai.app.aitask.task.wrapper;

import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;

import com.ai.app.aitask.task.ITask;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class QuartzTaskWrapper implements InterruptableJob {
    protected final static Logger log = Logger.getLogger(QuartzTaskWrapper.class);
    private ITask wrapped;

//    public QuartzTaskWrapper(ITask wrapped) {
//        this.wrapped = wrapped;
//    }
    
    public QuartzTaskWrapper() {
        log.debug("instance");
    }

    public final ITask getWrapped() {
        return wrapped;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("trying to execute");
        Map<String, Object> triggerMap = context.getTrigger().getJobDataMap();
        Map<String, Object> detailMap = context.getJobDetail().getJobDataMap();
        wrapped.execute(triggerMap, detailMap);
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        log.debug("interrupted");
        wrapped.interrupt();
    }
}

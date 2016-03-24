package com.ai.app.aitask.task.wrapper;

import java.util.Map;

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
    private ITask wrapped;

    public QuartzTaskWrapper(ITask wrapped) {
        this.wrapped = wrapped;
    }

    public final ITask getWrapped() {
        return wrapped;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Map<String, Object> triggerMap = context.getTrigger().getJobDataMap();
        Map<String, Object> detailMap = context.getJobDetail().getJobDataMap();
        wrapped.execute(triggerMap, detailMap);
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        wrapped.interrupt();
        // TODO
//        log.info("Serial Task interrup");
//        isJobInterrupted = true;
//        if (executor != null) {
//            log.info("interrupt executor: " + executor.getClass().toString());
//            executor.destroy();
//        }
//        if (thisThread != null) {
//            log.info("interrupt this thread: " + thisThread.getName());
//            thisThread.interrupt();
//        }
    }

}

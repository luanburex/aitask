package com.ai.app.aitask.listener.quartz;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.ai.app.aitask.common.TimeOutThread;

public class TimeoutListener implements JobListener {

    protected final static Logger log = Logger.getLogger(TimeoutListener.class);
    private TimeOutThread         _timeoutthread;

    @Override
    public String getName() {
        return "TaskTimeoutListener";
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.debug("[" + context.getTrigger().getKey().toString() + "]"
                + "the timeout listener is running(vote) for "
                + context.getJobDetail().getKey().getName());
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        _timeoutthread = new TimeOutThread(context);
        _timeoutthread.start();
        log.debug("[" + context.getTrigger().getKey().toString() + "]"
                + "the timeout listener is running for "
                + context.getJobDetail().getKey().getName());

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException arg1) {
        log.debug("[" + context.getTrigger().getKey().toString() + "]"
                + "the timeout linstener is normally ended.");
        if (_timeoutthread != null) {
            _timeoutthread.interrupt();
        }
    }
}

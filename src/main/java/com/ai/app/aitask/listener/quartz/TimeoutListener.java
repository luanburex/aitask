package com.ai.app.aitask.listener.quartz;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.ai.app.aitask.common.TimeOutThread;

public class TimeoutListener implements JobListener {

    protected static final Logger logger = Logger.getLogger(TimeoutListener.class);
    private TimeOutThread         _timeoutthread;

    @Override
    public String getName() {
        return "TaskTimeoutListener";
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        logger.debug("[" + context.getTrigger().getKey().toString() + "]"
                + "the timeout listener is running(vote) for "
                + context.getJobDetail().getKey().getName());
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        _timeoutthread = new TimeOutThread(context);
        _timeoutthread.start();
        logger.debug("[" + context.getTrigger().getKey().toString() + "]"
                + "the timeout listener is running for "
                + context.getJobDetail().getKey().getName());

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException arg1) {
        logger.debug("[" + context.getTrigger().getKey().toString() + "]"
                + "the timeout linstener is normally ended.");
        if (_timeoutthread != null) {
            _timeoutthread.interrupt();
        }
    }
}

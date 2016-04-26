package com.ai.app.aitask.listener.quartz;

import org.apache.log4j.Logger;
import org.quartz.Trigger;
import org.quartz.listeners.SchedulerListenerSupport;

public class ScheduleListner extends SchedulerListenerSupport {

    protected static final Logger logger = Logger.getLogger(ScheduleListner.class);

    @Override
    public void schedulerStarted() {
        logger.debug("Schduler Start.");
    }

    @Override
    public void schedulerShutdown() {
        logger.debug("Schduler Over.");
    }

    @Override
    public void jobScheduled(Trigger trigger) {
        // do something with the event, maybe.
        logger.debug("scheduled:"+trigger);
    }
}

package com.ai.app.aitask.listener;

import org.apache.log4j.Logger;
import org.quartz.Trigger;
import org.quartz.listeners.SchedulerListenerSupport;

public class OpenAndCloseListner extends SchedulerListenerSupport {

	private final static transient Logger log = Logger.getLogger(OpenAndCloseListner.class);

	
    @Override
    public void schedulerStarted() {
    	log.debug("Schduler Start.");
    }

    @Override
    public void schedulerShutdown() {
    	log.debug("Schduler Over.");
    }
    
    @Override
    public void jobScheduled(Trigger trigger) {
        // do something with the event
    }
}

package com.ai.app.aitask.task.tasks.impl;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;

import com.ai.app.aitask.task.parts.interfaces.IDataReparer;
import com.ai.app.aitask.task.parts.interfaces.IExecutor;
import com.ai.app.aitask.task.parts.interfaces.IResultFetcher;


@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class SerialTask implements InterruptableJob{
	
	transient final static private Logger log = Logger.getLogger(SerialTask.class);

	
	private volatile boolean isJobInterrupted = false;
	private volatile Thread  thisThread;
	private volatile IExecutor executor;

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		thisThread = Thread.currentThread();
		
		executor = (IExecutor)context.getMergedJobDataMap().get("executor");
		IDataReparer reparer = (IDataReparer)context.getMergedJobDataMap().get("reparer");
		IResultFetcher result = (IResultFetcher)context.getMergedJobDataMap().get("result");
		
		log.info("["+context.getTrigger().getKey()+"]"+"task execute....");
		
		if(executor == null) throw new JobExecutionException("The task's executor is null.");
		if(reparer!= null)
			reparer.parepare(context);
		
		executor.run(context);
		if(result != null)
			result.fetch(context);
	}

	public void interrupt() throws UnableToInterruptJobException {
		log.info("Serial Task interrup");
	  	isJobInterrupted = true;
	  	if(executor != null){
	  		log.info("interrupt executor: " + executor.getClass().toString() );
	  		executor.interupt();
	  	}
	    if (thisThread != null) {
	    	log.info("interrupt this thread: " + thisThread.getName() );
	    	thisThread.interrupt(); 
	    }
	}



	
}
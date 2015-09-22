package com.ai.app.aitask.task.tasks.impl;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;

import com.ai.app.aitask.task.parts.interfaces.IDataReparer;
import com.ai.app.aitask.task.parts.interfaces.IExecutor;
import com.ai.app.aitask.task.parts.interfaces.IResultFetcher;
import com.ai.app.aitask.task.tasks.ITask;


@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class SerialTask implements ITask{
	
	transient final static private Logger log = Logger.getLogger(SerialTask.class);

	
	private volatile boolean isJobInterrupted = false;
	private volatile Thread  thisThread;
	private volatile IExecutor executor;

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		thisThread = Thread.currentThread();
		try{
			context.getMergedJobDataMap().put("start_time", System.currentTimeMillis());
			
			executor = (IExecutor)context.getMergedJobDataMap().get("executor");
			IDataReparer reparer = (IDataReparer)context.getMergedJobDataMap().get("reparer");
			IResultFetcher result = (IResultFetcher)context.getMergedJobDataMap().get("result");
			
			log.info("["+context.getTrigger().getKey()+"]"+"task execute....");
			
			if(executor == null) throw new JobExecutionException("The task's executor is null.");
			if(reparer!= null && !isJobInterrupted)
				reparer.parepare(context);
			
			if(!isJobInterrupted){
				context.getMergedJobDataMap().put("start_time", System.currentTimeMillis());
				int executor_res = executor.run(context);
				context.getMergedJobDataMap().put("end_time", System.currentTimeMillis());
				log.info("["+context.getTrigger().getKey()+"]"+"task execute return: " + executor_res);
			}
			String result_str = null;
			if(result != null && !isJobInterrupted){
				result_str = result.fetch(context);
				log.info("["+context.getTrigger().getKey()+"]"+"task fetch result: " + result_str);
			}
		}catch(JobExecutionException je){
			IResultFetcher result = (IResultFetcher)context.getMergedJobDataMap().get("result");
			if(result != null && !isJobInterrupted){
				String result_str = result.error(context, je);
				log.info("["+context.getTrigger().getKey()+"]"+"task fetch result: " + result_str);
			}
		}
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

	public void before(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	public void after(JobExecutionContext context, JobExecutionException error) {
		// TODO Auto-generated method stub
		
	}



	
}
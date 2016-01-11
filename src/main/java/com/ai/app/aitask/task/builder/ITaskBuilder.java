package com.ai.app.aitask.task.builder;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;

public interface ITaskBuilder {

	public Trigger getTrigger();
	
	public JobDetail getJobDetail();
	
	public JobDataMap getDatamap() ;
	
	public void setDatamap(JobDataMap datamap) ;
	
	public void parseXml(String xml) throws Exception;
	
	public void generate() throws Exception;
}

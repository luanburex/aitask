package com.ai.app.aitask.task.tasks;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.ai.app.aitask.exception.TaskParseNotFoundException;
import com.ai.app.aitask.task.parts.interfaces.IDataReparer;
import com.ai.app.aitask.task.parts.interfaces.IExecutor;
import com.ai.app.aitask.task.parts.interfaces.IResultFetcher;
import com.ai.app.aitask.task.tasks.impl.SerialTask;

public abstract class AbstractTaskBuilder implements ITaskBuilder{
	
	transient final static private Logger log = Logger.getLogger(AbstractTaskBuilder.class);

	protected JobDetail jobDetail = null;
	protected Trigger trigger = null;
	protected JobDataMap datamap = new JobDataMap();
	
	protected IExecutor executor = null;
	protected IDataReparer repare = null;
	protected IResultFetcher result = null;
	
	
	public JobDataMap getDatamap() {
		return datamap;
	}
	public void setDatamap(JobDataMap datamap) {
		this.datamap = datamap;
	}
	public JobDetail getJobDetail() {
		return jobDetail;
	}
	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}
	public Trigger getTrigger() {
		return trigger;
	}
	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}
	public IExecutor getExecutor() {
		return executor;
	}
	public void setExecutor(IExecutor executor) {
		this.executor = executor;
	}
	public IDataReparer getRepare() {
		return repare;
	}
	public void setRepare(IDataReparer repare) {
		this.repare = repare;
	}
	public IResultFetcher getResult() {
		return result;
	}
	public void setResult(IResultFetcher result) {
		this.result = result;
	}
	

	public void parseXml(String xml) throws DocumentException, TaskParseNotFoundException{
		Document doc = DocumentHelper.parseText(xml);
        Element root = doc.getRootElement();
        
        //check
        if(root.attributeValue("ID") == null)
        	throw new TaskParseNotFoundException("");
        if(root.attributeValue("instant") == null)
        	throw new TaskParseNotFoundException("instant");
        if(root.attributeValue("cron") == null)
        	throw new TaskParseNotFoundException("cron");
        
        //set
		this.datamap.put("task_id", root.attributeValue("ID"));
		this.datamap.put("task_name", root.attributeValue("name", "noname"));
		this.datamap.put("task_category", root.attributeValue("category", "nocatory"));
		this.datamap.put("task_group", "AITASK");
		this.datamap.put("agent_id", root.attributeValue("agent_id", ""));
		this.datamap.put("agent_name", root.attributeValue("agent_name", ""));
		this.datamap.put("group_no", root.attributeValue("group_no", ""));
		this.datamap.put("group_name", root.attributeValue("group_name", ""));
		this.datamap.put("instant", root.attributeValue("instant", ""));
		this.datamap.put("timeout", Long.valueOf(root.attributeValue("timeout", "-1")));
		this.datamap.put("cron", root.attributeValue("cron", ""));

//		if(TaskCategory.CMD_TASK_TYPE.equals(root.attributeValue("ctype"))){
//			this.parseCmd(xml);
//		}else{
//			throw new TaskParseNotFoundException("ctype");
//		}
		
		datamap.put("reparer", this.repare);
		datamap.put("executor", this.executor);
		datamap.put("result", this.result);
	}
	
	
	public void generate(){
		
		
		String task_id = this.datamap.getString("task_id");
		String task_category = this.datamap.getString("task_category");
		String task_group = this.datamap.getString("task_group");
		String instant = this.datamap.getString("instant");
		String cron = this.datamap.getString("cron");
				
		JobKey jobKey = new JobKey(task_category, task_group);
		this.jobDetail = JobBuilder.newJob(SerialTask.class).withIdentity(jobKey).storeDurably().build();
		
		if("true".equals(instant))
			this.trigger = TriggerBuilder.newTrigger().withIdentity(
				task_id, task_group).startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule())
				.usingJobData(datamap)
				.build();
		else
			this.trigger = TriggerBuilder.newTrigger().withIdentity(
					task_id, task_group)
					.withSchedule(
							CronScheduleBuilder.cronSchedule(cron))
					.usingJobData(datamap)
					.build();
		
		log.debug("generate task jobKey:" + this.jobDetail.getKey() + "\t task trigger:" + this.trigger.getKey());
		
		
	}
}

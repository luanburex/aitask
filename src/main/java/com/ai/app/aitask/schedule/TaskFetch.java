package com.ai.app.aitask.schedule;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.Trigger.TriggerState;

import com.ai.app.aitask.common.HttpClient;
import com.ai.app.aitask.config.AgentProperties;
import com.ai.app.aitask.task.TaskDirector;
import com.ai.app.aitask.task.tasks.ITaskBuilder;

public class TaskFetch {
	
	transient final static Log log = LogFactory.getLog(TaskFetch.class);
	
	TaskSchedule ts = null;
	TaskSyncRunable task_sync = null;
	Thread task_sync_thread = null;
	
	public TaskFetch(TaskSchedule ts, long sync_task_interval_time){
		this.ts = ts;
		this.task_sync = new TaskSyncRunable(this.ts);
		this.task_sync.setIntervalTime(sync_task_interval_time);
		this.task_sync_thread = new Thread(this.task_sync);
		this.task_sync_thread.start();
	}
	
	
	
	public void fetch() throws Exception{
		String url = AgentProperties.getInstance().getProperty("aitask.task.sync.url");
		String agent_name = AgentProperties.getInstance().getProperty("aitask.name", AgentProperties.getInstance().getProperty("org.quartz.scheduler.instanceName"));
		
		url = url.trim();
		agent_name = agent_name.trim();
		String task_xml = HttpClient.post(url, "agent_name=" + agent_name, "x-www-form-urlencoded");
		System.out.println(task_xml);
		
		Document document = DocumentHelper.parseText(task_xml);
		Element root = document.getRootElement();
		
		Iterator<Element> elements = root.elementIterator("task");
		while(elements.hasNext()){
			Element current_element = elements.next();
			ITaskBuilder tb = TaskDirector.generateTaskBuilderByXml(current_element.asXML());
			if(TriggerState.BLOCKED.equals(this.ts.getTaskState(tb.getTrigger().getKey()))){
				log.debug("task is BLOCKED, add to sync queue." + tb.getTrigger().getKey());
				this.task_sync.putTask(tb.getTrigger().getKey(), current_element.asXML());
			}else{
				log.debug("add task:" + tb.getTrigger().getKey());
				this.ts.addTask(tb, true);
			}
		}
	}
	
	
}

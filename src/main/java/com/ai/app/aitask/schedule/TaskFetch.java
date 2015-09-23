package com.ai.app.aitask.schedule;

import java.io.IOException;

import com.ai.app.aitask.common.HttpClient;
import com.ai.app.aitask.config.AgentProperties;

public class TaskFetch {

	public static void fetch() throws IOException{
		String url = AgentProperties.getInstance().getProperty("aitask.task.sync.url");
		String agent_name = AgentProperties.getInstance().getProperty("aitask.name", AgentProperties.getInstance().getProperty("org.quartz.scheduler.instanceName"));
		
		url = url.trim();
		agent_name = agent_name.trim();
		HttpClient.post(url, "agent_name=" + agent_name, "x-www-form-urlencoded");
	}
	
	
}

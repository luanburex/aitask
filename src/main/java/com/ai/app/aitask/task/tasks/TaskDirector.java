package com.ai.app.aitask.task.tasks;

import com.ai.app.aitask.task.tasks.impl.CmdTaskBuilder;

public class TaskDirector {
	
	public static ITaskBuilder getCmdTaskBuilder(String xml) throws Exception{
		ITaskBuilder ctb = new CmdTaskBuilder();
		ctb.parseXml(xml);
		ctb.generate();
		return ctb;
	}

}

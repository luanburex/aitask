package com.ai.app.aitask.task;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ai.app.aitask.task.tasks.ITaskBuilder;
import com.ai.app.aitask.task.tasks.impl.BatTaskBuilder;
import com.ai.app.aitask.task.tasks.impl.CmdTaskBuilder;

public class TaskDirector {
	
	
	public static ITaskBuilder generateTaskBuilderByXml(String xml) throws Exception{
		Document document = DocumentHelper.parseText(xml);
		Element element = document.getRootElement();
		String ctype = element.attributeValue("ctype");
		if(TaskCategory.BAT_TASK_TYPE.equals(ctype))
			return TaskDirector.getBatTaskBuilder(xml);
		else if (TaskCategory.CMD_TASK_TYPE.equals(ctype))
			return TaskDirector.getCmdTaskBuilder(xml);
		
		return null;
		
	}
	
	public static ITaskBuilder getCmdTaskBuilder(String xml) throws Exception{
		ITaskBuilder ctb = new CmdTaskBuilder();
		ctb.parseXml(xml);
		ctb.generate();
		return ctb;
	}
	
	public static ITaskBuilder getBatTaskBuilder(String xml) throws Exception{
		ITaskBuilder ctb = new BatTaskBuilder();
		ctb.parseXml(xml);
		ctb.generate();
		return ctb;
	}

}

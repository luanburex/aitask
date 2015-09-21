package com.ai.app.aitask.task.tasks.impl;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ai.app.aitask.exception.TaskParseNotFoundException;
import com.ai.app.aitask.task.parts.impl.CmdTaskExecutor;
import com.ai.app.aitask.task.tasks.AbstractTaskBuilder;

public class CmdTaskBuilder extends AbstractTaskBuilder{
	
	transient final static private Logger log = Logger.getLogger(CmdTaskBuilder.class);

	@Override
	public void parseXml(String xml) throws DocumentException, TaskParseNotFoundException{
		Document doc = DocumentHelper.parseText(xml);
        Element root = doc.getRootElement();
        
        this.repare = null;
		this.executor = new CmdTaskExecutor(root.attributeValue("cmd_str"));
		this.result = null;
		super.parseXml(xml);
	}
	
}

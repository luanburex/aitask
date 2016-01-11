package com.ai.app.aitask.task.builder.impl;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ai.app.aitask.exception.TaskParseNotFoundException;
import com.ai.app.aitask.task.builder.AbstractTaskBuilder;
import com.ai.app.aitask.task.excutor.impl.BatTaskExecutor;
import com.ai.app.aitask.task.excutor.impl.IniResultFetcher;

public class BatTaskBuilder extends AbstractTaskBuilder{
	
	transient final static private Logger log = Logger.getLogger(BatTaskBuilder.class);

	@Override
	public void parseXml(String xml) throws DocumentException, TaskParseNotFoundException{
		Document doc = DocumentHelper.parseText(xml);
        Element root = doc.getRootElement();
        
        Element case_element = root.element("case");
        
        
        BatTaskExecutor executor = new BatTaskExecutor(
        		case_element.attributeValue("bat_path"),
        		case_element.attributeValue("project_path"),
        		case_element.attributeValue("script_path"),
        		case_element.attributeValue("ini_path")
        		);
        IniResultFetcher fetcher = new IniResultFetcher(case_element.attributeValue("ini_path"));
        
        
        this.preparer = null;
		this.executor = executor;
		this.result = fetcher;
		super.parseXml(xml);
	}
}

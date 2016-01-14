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

public class BatTaskBuilder extends AbstractTaskBuilder {

    protected final static Logger log = Logger.getLogger(BatTaskBuilder.class);

    @Override
    public void parseXml(String xml) throws DocumentException, TaskParseNotFoundException {
        Document doc = DocumentHelper.parseText(xml);
        Element case_element = doc.getRootElement().element("case");

        String path_bat = case_element.attributeValue("bat_path");
        String path_ini = case_element.attributeValue("ini_path");
        String path_script = case_element.attributeValue("script_path");
        String path_project = case_element.attributeValue("project_path");
        BatTaskExecutor exe = new BatTaskExecutor(path_bat, path_project, path_script, path_ini);
        IniResultFetcher fetcher = new IniResultFetcher(case_element.attributeValue("ini_path"));

        this.preparer = null;
        this.executor = exe;
        this.result = fetcher;
        super.parseXml(xml);
    }
}
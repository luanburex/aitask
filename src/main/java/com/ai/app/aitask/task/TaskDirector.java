package com.ai.app.aitask.task;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ai.app.aitask.task.builder.ITaskBuilder;
import com.ai.app.aitask.task.builder.impl.BatTaskBuilder;
import com.ai.app.aitask.task.builder.impl.CmdTaskBuilder;

public class TaskDirector implements TaskCategory {

    public static ITaskBuilder generateTaskBuilderByXml(String xml) throws Exception {
        Document document = DocumentHelper.parseText(xml);
        Element element = document.getRootElement();
        switch (Integer.parseInt(element.attributeValue("ctype"))) {
        case TASK_TYPE_BAT:
            return getBatTaskBuilder(xml);
        case TASK_TYPE_CMD:
            return getCmdTaskBuilder(xml);
        }
        return null;
    }

    public static ITaskBuilder getCmdTaskBuilder(String xml) throws Exception {
        ITaskBuilder ctb = new CmdTaskBuilder();
        ctb.parseXml(xml);
        ctb.generate();
        return ctb;
    }

    public static ITaskBuilder getBatTaskBuilder(String xml) throws Exception {
        ITaskBuilder ctb = new BatTaskBuilder();
        ctb.parseXml(xml);
        ctb.generate();
        return ctb;
    }

}

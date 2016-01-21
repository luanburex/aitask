package com.ai.app.aitask.task.builder.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ai.app.aitask.task.builder.AbstractTaskBuilder;
import com.ai.app.aitask.task.excutor.impl.CmdTaskExecutor;

public class CmdTaskBuilder extends AbstractTaskBuilder {

    protected final static Logger log = Logger.getLogger(CmdTaskBuilder.class);

    @Override
    public void parseTask(Map<String, Object> datamap) throws Exception {
        jobDatamap.put("preparer", null);
        jobDatamap.put("executor", new CmdTaskExecutor((String) jobDatamap.get("cmd_str")));
        jobDatamap.put("result", null);
        super.parseTask(datamap);
    }

}

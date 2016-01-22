package com.ai.app.aitask.task.builder.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ai.app.aitask.task.builder.AbstractTaskBuilder;
import com.ai.app.aitask.task.excutor.impl.CmdTaskExecutor;
import com.ai.app.aitask.task.result.impl.IniResultFetcher;
import com.ai.app.aitask.task.result.impl.WTFResultFetcher;

public class CmdTaskBuilder extends AbstractTaskBuilder {

    protected final static Logger log = Logger.getLogger(CmdTaskBuilder.class);

    @Override
    public void parseTask(Map<String, Object> datamap) throws Exception {
        jobDatamap.put("preparer", null);
        String cmd_str = "ping www.baidu.com";
        jobDatamap.put("executor", new CmdTaskExecutor(cmd_str));
        //        jobDatamap.put("executor", new CmdTaskExecutor((String) jobDatamap.get("cmd_str")));
        jobDatamap.put("result", new WTFResultFetcher());
        super.parseTask(datamap);
    }

}

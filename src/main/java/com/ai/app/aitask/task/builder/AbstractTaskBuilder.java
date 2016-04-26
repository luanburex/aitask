package com.ai.app.aitask.task.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ai.app.aitask.task.excutor.IExecutor;
import com.ai.app.aitask.task.preparer.IDataPreparer;
import com.ai.app.aitask.task.result.IResultFetcher;

/**
 * @author renzq
 * @author Alex Xu
 */
public abstract class AbstractTaskBuilder implements ITaskBuilder {
    protected static final Logger logger = Logger.getLogger(AbstractTaskBuilder.class);

    protected Map<String, Object> datamap;
    protected Map<String, Object> content;
    protected Map<String, Object> key;

    protected IExecutor           executor;
    protected IDataPreparer       preparer;
    protected IResultFetcher      fetcher;

    public AbstractTaskBuilder(Map<String, Object> datamap) {
        this.datamap = datamap;
        this.key = new HashMap<String, Object>();
        this.content = new HashMap<String, Object>();
    }
    
    @Override
    public void build() {
        content.put("executor", executor);
        content.put("preparer", preparer);
        content.put("fetcher", fetcher);
    }
    @Override
    public Map<String, Object> getTrigger() {
        return key;
    }
    @Override
    public Map<String, Object> getContent() {
        return content;
    }
}

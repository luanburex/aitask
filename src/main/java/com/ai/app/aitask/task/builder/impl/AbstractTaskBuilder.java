package com.ai.app.aitask.task.builder.impl;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ai.app.aitask.task.builder.ITaskBuilder;
import com.ai.app.aitask.task.executor.IExecutor;
import com.ai.app.aitask.task.executor.impl.DefaultExecutor;
import com.ai.app.aitask.task.fetcher.IResultFetcher;
import com.ai.app.aitask.task.fetcher.impl.DefaultResultFetcher;
import com.ai.app.aitask.task.preparer.IDataPreparer;
import com.ai.app.aitask.task.preparer.impl.DefaultDataPreparer;

/**
 * @author renzq
 * @author Alex Xu
 */
public abstract class AbstractTaskBuilder implements ITaskBuilder {
    protected static final Logger       logger           = Logger.getLogger(AbstractTaskBuilder.class);
    private static final IDataPreparer  DEFAULT_PREPARER = new DefaultDataPreparer();
    private static final IExecutor      DEFAULT_EXECUTOR = new DefaultExecutor();
    private static final IResultFetcher DEFAULT_FETCHER  = new DefaultResultFetcher();

    protected Map<String, String>       properties;
    protected Map<String, Object>       datamap;
    protected Map<String, Object>       content          = new HashMap<String, Object>();
    protected Map<String, Object>       key              = new HashMap<String, Object>();

    public AbstractTaskBuilder(Map<String, Object> datamap, Map<String, String> properties) {
        this.datamap = datamap;
        this.properties = properties;
    }

    @Override
    public ITaskBuilder build() {
        IDataPreparer preparer = getPreparer();
        IExecutor executor = getExecutor();
        IResultFetcher fetcher = getFetcher();
        content.put("preparer", preparer);
        content.put("executor", executor);
        content.put("fetcher", fetcher);
        logger.info("preparer : " + preparer.getClass().getSimpleName());
        logger.info("executor : " + executor.getClass().getSimpleName());
        logger.info("fetcher : " + fetcher.getClass().getSimpleName());
        return this;
    }
    protected IDataPreparer getPreparer() {
        return instance(properties.get("preparer"), IDataPreparer.class, DEFAULT_PREPARER);
    }

    protected IExecutor getExecutor() {
        return instance(properties.get("executor"), IExecutor.class, DEFAULT_EXECUTOR);
    }

    protected IResultFetcher getFetcher() {
        return instance(properties.get("fetcher"), IResultFetcher.class, DEFAULT_FETCHER);
    }

    private <T> T instance(String clsName, Class<T> cls, T defaultInstance) {
        try {
            Class<? extends T> actCls = Class.forName(clsName).asSubclass(cls);
            Constructor<?> constructor = actCls.getConstructors()[0];
            switch (constructor.getParameterTypes().length) {
            case 0:
                return actCls.cast(constructor.newInstance());
            case 1:
                return actCls.cast(constructor.newInstance(properties));
            default:
                throw new IllegalArgumentException("too much parameters needed");
            }
        } catch (Exception e) {
            logger.debug(String.format("instance %s as %s failed.", clsName, cls.getSimpleName()));
            return defaultInstance;
        }
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

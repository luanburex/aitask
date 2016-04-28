package com.ai.app.aitask.task.fetcher.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.common.OrderedProperties;
import com.ai.app.aitask.task.fetcher.IResultFetcher;

/**
 * @author renzq
 * @author Alex Xu
 */
public class IniResultFetcher implements IResultFetcher {
    protected static final Logger logger = Logger.getLogger(IniResultFetcher.class);
    private Map<String, String>   properties;
    /*
     * TODO 这个类可以通过配置更加抽象
     */
    public IniResultFetcher(Map<String, String> properties) {
        this.properties = properties;
    }
    @Override
    public Map<String, Object> fetch(Map<String, Object> datamap) {
        Config ini = Config.instance(properties.get("ini"));
        //TODO do your work
        OrderedProperties results = ini.getProperties("探测结果");
        logger.info("ini fetching : " + "探测结果");
        logger.info(Constants.GSON.toJson(Config.asMap(results)));

        Map<String, Object> resultMap = new HashMap<String, Object>();
        return resultMap;
    }
    @Override
    public Map<String, Object> error(Map<String, Object> datamap, Exception exception) {
        // TODO Auto-generated method stub
        exception.getMessage();
        return null;
    }
}

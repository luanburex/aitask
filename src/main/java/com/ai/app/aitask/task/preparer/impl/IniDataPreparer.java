package com.ai.app.aitask.task.preparer.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.common.OrderedProperties;
import com.ai.app.aitask.task.preparer.IDataPreparer;

/**
 * @author Alex Xu
 */
public class IniDataPreparer implements IDataPreparer {
    protected static final Logger logger = Logger.getLogger(IniDataPreparer.class);
    private Map<String, String>   properties;
    /*
     * TODO 这个类可以通过配置更加抽象
     */
    public IniDataPreparer(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public void prepare(Map<String, Object> datamap) {
        Config ini = Config.instance(properties.get("ini"));
        //TODO do your work
        OrderedProperties timeouts = ini.getProperties("超时参数");  // 探测结果
        logger.info("ini preparering : " + "超时参数");
        logger.info(Constants.GSON.toJson(Config.asMap(timeouts)));
        ini.write();
    }
}

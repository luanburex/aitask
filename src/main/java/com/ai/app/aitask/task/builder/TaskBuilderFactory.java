package com.ai.app.aitask.task.builder;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.task.parser.ITaskParser;

/**
 * @author renzq
 * @author Alex Xu
 */
public class TaskBuilderFactory implements Constants {
    protected static final Logger                                            logger;
    protected static Map<String, Constructor<? extends AbstractTaskBuilder>> constructorMap;
    protected static Map<String, Map<String, String>>                        propertiesMap;
    protected static ITaskParser                                             taskParser;
    static {
        logger = Logger.getLogger(TaskBuilderFactory.class);
        propertiesMap = new HashMap<String, Map<String, String>>();
        constructorMap = new HashMap<String, Constructor<? extends AbstractTaskBuilder>>();
        Properties properties = Config.instance(CONFIG_AITASK).getProperties(null);
        for (String name : properties.stringPropertyNames()) {
            if (name.startsWith("builder")) {
                String[] part = name.split("\\.");
                if (3 == part.length) {
                    Map<String, String> map;
                    if (null == (map = propertiesMap.get(part[1]))) {
                        propertiesMap.put(part[1], map = new HashMap<String, String>());
                    }
                    map.put(part[2], properties.getProperty(name));
                }
            }
        }
        for (Entry<String, Map<String, String>> builders : propertiesMap.entrySet()) {
            String className = builders.getValue().get("class");
            try {
                Class<? extends AbstractTaskBuilder> builderClass;
                builderClass = Class.forName(className).asSubclass(AbstractTaskBuilder.class);
                constructorMap.put(builders.getKey(), builderClass.getConstructor(Map.class));
                logger.info("TaskBuilder Added : " + builders.getKey() + "/" + className);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Class<?> parserClass;
        try {
            parserClass = Class.forName(properties.getProperty("parser"));
            taskParser = parserClass.asSubclass(ITaskParser.class).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<ITaskBuilder> parseBuilder(String content) {
        List<ITaskBuilder> builderList = new LinkedList<ITaskBuilder>();
        for (Map<String, Object> dataMap : taskParser.parseContent(content)) {
            Map<String, Object> scriptMap = Caster.cast(dataMap.get("script"));
            String scriptType = (String) scriptMap.get("scriptType");
            dataMap.put("properties", propertiesMap.get(scriptType));
            try {
                ITaskBuilder builder = constructorMap.get(scriptType).newInstance(dataMap);
                builder.build();
                builderList.add(builder);
                logger.info("build task : " + scriptType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return builderList;
    }
}

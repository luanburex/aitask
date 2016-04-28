package com.ai.app.aitask.task;

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
import com.ai.app.aitask.task.builder.ITaskBuilder;
import com.ai.app.aitask.task.parser.ITaskParser;

/**
 * @author renzq
 * @author Alex Xu
 */
public class TaskBuilderFactory implements Constants {
    protected static final Logger logger  = Logger.getLogger(TaskBuilderFactory.class);
    private static final Manager  manager = new Manager();

    public static List<ITaskBuilder> parseBuilder(String content) {
        List<ITaskBuilder> builderList = new LinkedList<ITaskBuilder>();
        for (Map<String, Object> dataMap : manager.parseContent(content)) {
            Map<String, Object> scriptMap = Caster.cast(dataMap.get("script"));
            String scriptType = (String) scriptMap.get("scriptType");
            try {
                logger.info("building task : " + scriptType);
                builderList.add(manager.getBuilder(dataMap, scriptType).build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return builderList;
    }

    private static class Manager {
        Map<String, Constructor<? extends ITaskBuilder>> constructorMap;
        Map<String, Map<String, String>>                 propertiesMap;
        ITaskParser                                      taskParser;
        Manager() {
            propertiesMap = new HashMap<String, Map<String, String>>();
            constructorMap = new HashMap<String, Constructor<? extends ITaskBuilder>>();
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
                    Class<? extends ITaskBuilder> cls;
                    cls = Class.forName(className).asSubclass(ITaskBuilder.class);
                    constructorMap.put(builders.getKey(), cls.getConstructor(Map.class, Map.class));
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

        public ITaskBuilder getBuilder(Map<String, Object> dataMap, String scriptType) {
            try {
                Constructor<? extends ITaskBuilder> constructor = constructorMap.get(scriptType);
                return constructor.newInstance(dataMap, propertiesMap.get(scriptType));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public List<Map<String, Object>> parseContent(String content) {
            return taskParser.parseContent(content);
        }
    }
}

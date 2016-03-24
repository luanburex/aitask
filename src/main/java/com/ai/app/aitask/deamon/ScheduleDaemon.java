package com.ai.app.aitask.deamon;

import org.apache.log4j.Logger;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.schedule.ITaskScheduler;

public class ScheduleDaemon {
    protected final static Logger log = Logger.getLogger(ScheduleDaemon.class);

    private static ScheduleDaemon instance;
    private ITaskScheduler        scheduler;

    private ScheduleDaemon() {
        Config config = Config.instance("aitask.properties");
        try {
            Class<?> schedulerClass = Class.forName(config.getProperty(null, "scheduler"));
            scheduler = schedulerClass.asSubclass(ITaskScheduler.class).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ScheduleDaemon instance() {
        if (null == instance) {
            return instance = new ScheduleDaemon();
        } else {
            return instance;
        }
    }

    public ITaskScheduler getScheduler() {
        return scheduler;
    }

    public void start() {
        scheduler.start();
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}

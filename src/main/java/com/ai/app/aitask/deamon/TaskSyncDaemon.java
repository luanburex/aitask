package com.ai.app.aitask.deamon;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.schedule.TaskFetcher;

public class TaskSyncDaemon {
    private final static transient Logger log = Logger.getLogger(ScheduleDaemon.class);
    private static TaskSyncDaemon         singleton;
    private TaskFetcher                     fetcher;
    public static synchronized TaskSyncDaemon instance() {
        if (null == singleton) {
            return singleton = new TaskSyncDaemon();
        } else {
            return singleton;
        }
    }
    private TaskSyncDaemon() {
        Config config = Config.instance("client.properties");
        long interval = Long.parseLong(config.getProperty(null, "aitask.sync.interval"));
        log.info("interval:" + interval);
        fetcher = new TaskFetcher(ScheduleDaemon.instance().getTaskSchedule(), interval);
    }
    public void start() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("try task synchronize");
                fetcher.fetch();
            }
        }, 100l, 1000l);
    }
}

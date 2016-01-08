package com.ai.app.aitask.deamon;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.ai.app.aitask.common.ConfigurationFile;
import com.ai.app.aitask.schedule.TaskFetch;

public class TaskSyncDaemon {
    private final static transient Logger log = Logger.getLogger(ScheduleDaemon.class);
//    private ConfigurationFile             config;
    private TaskFetch                     fetcher;
    private ScheduleDaemon                schedule;

    public TaskSyncDaemon(ConfigurationFile config) {
//        this.config = config;
        long interval = Long.parseLong(config.getValue(null, "aitask.sync.interval"));
        schedule = ScheduleDaemon.instance();
        fetcher = new TaskFetch(config, schedule.getTaskSchedule(), interval);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("try task synchronize");
                try {
                    fetcher.fetch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 100l, 1000l);
    }
}

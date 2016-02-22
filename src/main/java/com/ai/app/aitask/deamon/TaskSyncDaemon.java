package com.ai.app.aitask.deamon;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

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
        fetcher = new TaskFetcher(ScheduleDaemon.instance().getTaskSchedule());
    }
    public void start() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("try task synchronize");
                fetcher.fetch(null, null);
            }
        }, 100l, 1000l);
    }
}

package com.ai.app.aitask.deamon;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.ai.app.aitask.schedule.TaskFetcher;
import com.ai.app.aitask.schedule.TaskSyncRunable;
import com.ai.app.aitask.task.builder.ITaskBuilder;

public class TaskSyncDaemon {
    protected final static Logger log = Logger.getLogger(ScheduleDaemon.class);
    private static TaskSyncDaemon instance;
    private TaskSyncRunable       syncRunnable;
    private Timer                 timer;

    public static synchronized TaskSyncDaemon instance() {
        return null == instance ? instance = new TaskSyncDaemon() : instance;
    }

    private TaskSyncDaemon() {
        syncRunnable = new TaskSyncRunable();
    }

    public void syncTask(List<ITaskBuilder> tasks) {
        for (ITaskBuilder task : tasks) {
            syncRunnable.offerTask(task);
        }
    }

    public void start() {
        new Thread(syncRunnable).start();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            private TaskFetcher fetcher = new TaskFetcher();
            @Override
            public void run() {
                log.info("try synchronize tasklist");
                syncTask(fetcher.fetch(null, null));
            }
        }, 100l, 1000l);
    }

    public void shutdown() {
        timer.cancel();
    }
}

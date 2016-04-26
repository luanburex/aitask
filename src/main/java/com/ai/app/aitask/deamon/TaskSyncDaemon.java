package com.ai.app.aitask.deamon;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.ai.app.aitask.schedule.TaskFetcher;
import com.ai.app.aitask.schedule.TaskSyncRunable;
import com.ai.app.aitask.task.builder.ITaskBuilder;

/**
 * @author renzq
 */
public class TaskSyncDaemon {
    protected static final Logger logger = Logger.getLogger(ScheduleDaemon.class);
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
        logger.info("try synchronize tasklist : " + tasks.size());
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
                syncTask(fetcher.fetch(null, null));
            }
        }, 100l, 1000l);
    }

    public void shutdown() {
        timer.cancel();
    }
}

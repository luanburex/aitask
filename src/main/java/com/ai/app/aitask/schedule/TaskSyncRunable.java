package com.ai.app.aitask.schedule;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.task.builder.ITaskBuilder;

public class TaskSyncRunable implements Runnable {

    protected final static Log  log = LogFactory.getLog(TaskSyncRunable.class);

    private Config              config;
    private long                intervalTime;
    private Queue<ITaskBuilder> taskSyncQueue;
    private ITaskScheduler      taskScheduler;

    public TaskSyncRunable() {
        this.config = Config.instance("client.properties");
        this.intervalTime = Long.parseLong(config.getProperty(null, "aitask.sync.interval"));
        this.taskSyncQueue = new ConcurrentLinkedQueue<ITaskBuilder>();
        this.taskScheduler = ScheduleDaemon.instance().getScheduler();
    }

    public void offerTask(ITaskBuilder task) {
        // TODO keys & info & states
        Object state = taskScheduler.getTaskState(task.getTrigger());
        if (taskScheduler.addTask(task, true)) {
            log.debug("task in sync : " + task.getTrigger().get("info") + state);
        } else {
            taskSyncQueue.offer(task);
            log.debug("task out of sync : " + task.getTrigger().get("info") + state);
        }

    }

    private void sync() {
        for (ITaskBuilder task : taskSyncQueue) {
            if (taskScheduler.addTask(task, true)) {
                // TODO keys & info & states
                Object state = taskScheduler.getTaskState(task.getTrigger());
                log.debug("sync in task : " + task.getTrigger().get("info") + state);
                taskSyncQueue.remove(task);
            }
        }
    }

    @Override
    public void run() {
        if (intervalTime < 1) {
            log.info(String.format("task sync unabled (%d)", intervalTime));
        } else {
            log.info(String.format("task sync enabled (%d)", intervalTime));
            try {
                while (true) {
                    log.info("task sync");
                    sync();
                    Thread.sleep(intervalTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
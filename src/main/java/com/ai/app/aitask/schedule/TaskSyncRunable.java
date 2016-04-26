package com.ai.app.aitask.schedule;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.listener.TaskSyncListener;
import com.ai.app.aitask.task.builder.ITaskBuilder;

/**
 * @author renzq
 */
public class TaskSyncRunable implements Runnable, Constants {

    protected static final Log  logger = LogFactory.getLog(TaskSyncRunable.class);

    private TaskSyncListener    taskSyncListener;
    private Queue<ITaskBuilder> taskSyncQueue;
    private ITaskScheduler      taskScheduler;
    private long                intervalTime;
    private Config              config;

    public TaskSyncRunable() {
        config = Config.instance(CONFIG_CLIENT);
        intervalTime = Long.parseLong(config.getProperty("aitask.sync.interval"));
        taskSyncQueue = new ConcurrentLinkedQueue<ITaskBuilder>();
        taskScheduler = ScheduleDaemon.instance().getScheduler();
        taskSyncListener = new TaskSyncListener();
    }

    public void offerTask(ITaskBuilder task) {
        if (!taskSyncListener.beforeTaskSchedule(task) && taskScheduler.addTask(task, true)) {
            taskSyncListener.afterTaskSchedule(task, true);
        } else if (!taskSyncListener.beforeTaskEnqueue(task)) {
            taskSyncQueue.offer(task);
            taskSyncListener.afterTaskEnqueue(task, true);
        }
    }

    private void sync() {
        for (ITaskBuilder task : taskSyncQueue) {
            if (!taskSyncListener.beforeTaskSchedule(task) && taskScheduler.addTask(task, true)) {
                taskSyncListener.afterTaskSchedule(task, true);
                taskSyncQueue.remove(task);
            }
        }
    }
    @Override
    public void run() {
        if (intervalTime < 1) {
            logger.info(String.format("task sync disabled (%d)", intervalTime));
        } else {
            logger.info(String.format("task sync enabled (%d)", intervalTime));
            try {
                while (true) {
                    logger.info("task sync");
                    sync();
                    Thread.sleep(intervalTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
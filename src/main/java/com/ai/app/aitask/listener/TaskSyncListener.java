package com.ai.app.aitask.listener;

import org.apache.log4j.Logger;

import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.schedule.ITaskScheduler;
import com.ai.app.aitask.task.builder.ITaskBuilder;

/**
 * @author Alex Xu
 */
public class TaskSyncListener {
    protected static final Logger logger = Logger.getLogger(TaskSyncListener.class);
    private ITaskScheduler        taskScheduler;

    public TaskSyncListener() {
        taskScheduler = ScheduleDaemon.instance().getScheduler();
    }

    public boolean beforeTaskEnqueue(ITaskBuilder task) {
        boolean vetoed = false;
        logger.info("Task Enqueue Vetoed : " + vetoed);
        return vetoed;
    }

    public void afterTaskEnqueue(ITaskBuilder task, boolean succeed) {
        Object state = taskScheduler.getTaskState(task.getTrigger());
        logger.debug("task enqueue : " + task.getTrigger().get("info") + state);
    }

    public boolean beforeTaskSchedule(ITaskBuilder task) {
        boolean vetoed = false;
        logger.info("Task Schedule Vetoed : " + vetoed);
        return vetoed;
    }

    public void afterTaskSchedule(ITaskBuilder task, boolean succeed) {
        Object state = taskScheduler.getTaskState(task.getTrigger());
        logger.debug("task scheduled : " + task.getTrigger().get("info") + state);
    }
}

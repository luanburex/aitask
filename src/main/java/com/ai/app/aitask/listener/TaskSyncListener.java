package com.ai.app.aitask.listener;

import org.apache.log4j.Logger;

import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.schedule.ITaskScheduler;
import com.ai.app.aitask.task.builder.ITaskBuilder;

/**
 * @author Alex Xu
 */
public class TaskSyncListener {
    protected final static Logger log = Logger.getLogger(TaskSyncListener.class);
    private ITaskScheduler        taskScheduler;

    public TaskSyncListener() {
        taskScheduler = ScheduleDaemon.instance().getScheduler();
    }

    public boolean beforeTaskEnqueue(ITaskBuilder task) {
        return false;
    }

    public void afterTaskEnqueue(ITaskBuilder task, boolean succeed) {
        Object state = taskScheduler.getTaskState(task.getTrigger());
        log.debug("task enqueue : " + task.getTrigger().get("info") + state);
    }

    public boolean beforeTaskSchedule(ITaskBuilder task) {
        return false;
    }

    public void afterTaskSchedule(ITaskBuilder task, boolean succeed) {
        Object state = taskScheduler.getTaskState(task.getTrigger());
        log.debug("task scheduled : " + task.getTrigger().get("info") + state);
    }
}

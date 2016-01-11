package com.ai.app.aitask.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

import com.ai.app.aitask.task.TaskDirector;
import com.ai.app.aitask.task.builder.ITaskBuilder;

public class TaskSyncRunable implements Runnable {

    final transient static Log log           = LogFactory.getLog(TaskSyncRunable.class);

    Map<TriggerKey, String>    tasks         = new ConcurrentHashMap<TriggerKey, String>();
    TaskSchedule               taskSchedule  = null;
    long                       interval_time = 1000l;

    public TaskSyncRunable(TaskSchedule taskSchedule) {
        this.taskSchedule = taskSchedule;
    }

    public void setIntervalTime(long time) {
        this.interval_time = time;
    }

    public void putTask(TriggerKey key, String xml) {
        tasks.put(key, xml);
    }

    public synchronized void sync() throws Exception {
        for (TriggerKey key : tasks.keySet()) {
            if (!TriggerState.BLOCKED.equals(taskSchedule.getTaskState(key))) {
                log.debug("sync task: " + key + " , status: " + taskSchedule.getTaskState(key));
                ITaskBuilder tb = TaskDirector.generateTaskBuilderByXml(tasks.get(key));
                this.taskSchedule.addTask(tb, true);
                tasks.remove(key);
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(interval_time);
                log.info("sleep:"+interval_time);
                this.sync();
            }
        } catch (Exception e) {
            log.error(e);
        }
    }
}
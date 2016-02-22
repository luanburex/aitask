package com.ai.app.aitask.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.task.builder.ITaskBuilder;

public class TaskSyncRunable implements Runnable {

    final transient static Log    log          = LogFactory.getLog(TaskSyncRunable.class);

    Map<TriggerKey, ITaskBuilder> tasks        = new ConcurrentHashMap<TriggerKey, ITaskBuilder>();
    TaskSchedule                  taskSchedule = null;
    long                          intervalTime = 1000l;

    public TaskSyncRunable(TaskSchedule taskSchedule) {
        Config config = Config.instance("client.properties");
        this.intervalTime = Long.parseLong(config.getProperty(null, "aitask.sync.interval"));
        this.taskSchedule = taskSchedule;
    }

    public void putTask(ITaskBuilder tb) {
        tasks.put(tb.getTrigger().getKey(), tb);
    }

    public synchronized void sync() throws Exception {
        for (TriggerKey key : tasks.keySet()) {
            if (!TriggerState.BLOCKED.equals(taskSchedule.getTaskState(key))) {
                log.debug("sync task: " + key + " , status: " + taskSchedule.getTaskState(key));
                this.taskSchedule.addTask(tasks.get(key), true);
                tasks.remove(key);
            }
        }
    }

    @Override
    public void run() {
        if (intervalTime < 1) {
            log.info("task sync closed (" + intervalTime + ")");
        } else {
            try {
                while (true) {
                    Thread.sleep(intervalTime);
                    log.info("sleep:" + intervalTime);
                    this.sync();
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
}
package com.ai.app.aitask.schedule.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.listener.quartz.ScheduleListner;
import com.ai.app.aitask.schedule.ITaskScheduler;
import com.ai.app.aitask.task.builder.ITaskBuilder;
import com.ai.app.aitask.task.wrapper.QuartzTaskWrapper;

/**
 * @author renzq
 * @author Alex Xu
 */
public class QuartzScheduler implements ITaskScheduler {
    protected final static Logger log = Logger.getLogger(QuartzScheduler.class);
    private Scheduler             scheduler;
    private String                name;
    public QuartzScheduler() throws SchedulerException {
        Config config = Config.instance(CONFIG_QUARTZ);
        scheduler = new StdSchedulerFactory(config.getProperties(null)).getScheduler();
        name = scheduler.getSchedulerName();
        scheduler.standby();
        log.debug(String.format("[%s] new Scheduler : %s", name, getClass()));
        scheduler.getListenerManager().addSchedulerListener(new ScheduleListner());
    }
    @Override
    public void start() {
        try {
            scheduler.start();
            log.debug(String.format("[%s] Scheduler start", name));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void shutdown() {
        try {
            scheduler.shutdown();
            log.debug(String.format("[%s] Scheduler shutdown", name));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean addTask(ITaskBuilder taskBuilder, boolean replace) {
        try {
            int count = scheduler.getCurrentlyExecutingJobs().size();
            log.debug(String.format("[%s] current running task : %d", name, count));
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }

        Trigger trigger = parseTrigger(taskBuilder.getTrigger());
        JobDetail detail = parseDetail(trigger.getKey(), taskBuilder.getContent());
        TriggerState state = (TriggerState) getTaskState(trigger.getKey());
        if (TriggerState.BLOCKED == state) {
            log.debug(String.format("[%s] block task : %s @ %s", name, trigger.getKey(), state));
            return false;
        } else {
            try {
                scheduler.scheduleJob(detail, trigger);

                Date fire = trigger.getNextFireTime();
                state = (TriggerState) getTaskState(trigger.getKey());

                log.debug(String.format("[%s] add task : %s @ %s", name, trigger.getKey(), state));
                log.debug(String.format("[%s] run task : %s @ %s", name, trigger.getKey(), fire));
                return true;
            } catch (SchedulerException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private Trigger parseTrigger(Map<String, Object> triggerMap) {
        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger();
        builder.withIdentity((String) triggerMap.get("key"), (String) triggerMap.get("group"));
        if ((Boolean) triggerMap.get("instant")) {
            log.debug("instant trigger");
            builder.withSchedule(SimpleScheduleBuilder.simpleSchedule()).startNow();
        } else {
            log.debug("cron trigger");
            builder.withSchedule(CronScheduleBuilder.cronSchedule((String) triggerMap.get("cron")));
        }
        return builder.usingJobData(new JobDataMap(triggerMap)).build();
    }

    private JobDetail parseDetail(TriggerKey key, Map<String, Object> contentMap) {
        JobBuilder builder = JobBuilder.newJob(QuartzTaskWrapper.class);
        builder.withIdentity(key.getName(), key.getGroup());
        builder.storeDurably();
        return builder.usingJobData(new JobDataMap(contentMap)).build();
    }
    @Override
    public Object getTaskState(Object... keys) {
        TriggerKey key;
        if (1 == keys.length && keys[0] instanceof TriggerKey) {
            key = (TriggerKey) keys[0];
        } else if (2 == keys.length && keys[0] instanceof String && keys[1] instanceof String) {
            key = new TriggerKey((String) keys[0], (String) keys[1]);
        } else {
            return null;
        }
        try {
            return scheduler.getTriggerState(key);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void fireTask(Object... keys) {
        Trigger trigger;
        if (1 == keys.length && keys[0] instanceof Trigger) {
            trigger = (Trigger) keys[0];
        } else {
            trigger = (Trigger) getTrigger(keys);
        }
        try {
            scheduler.triggerJob(trigger.getJobKey());
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void suspendTask(Object... keys) {
        JobKey key;
        if (1 == keys.length && keys[0] instanceof JobKey) {
            key = (JobKey) keys[0];
        } else if (1 == keys.length && keys[0] instanceof TriggerKey) {
            try {
                key = scheduler.getTrigger((TriggerKey) keys[0]).getJobKey();
            } catch (SchedulerException e) {
                e.printStackTrace();
                return;
            }
        } else if (2 == keys.length && keys[0] instanceof String && keys[1] instanceof String) {
            key = new JobKey((String) keys[0], (String) keys[1]);
        } else {
            return;
        }
        try {
            scheduler.interrupt(key);
        } catch (UnableToInterruptJobException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Object getTrigger(Object... keys) {
        Trigger trigger = null;
        if (1 == keys.length && keys[0] instanceof TriggerKey) {
            try {
                trigger = scheduler.getTrigger(((TriggerKey) keys[0]));
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        } else if (2 == keys.length && keys[0] instanceof String && keys[1] instanceof String) {
            try {
                trigger = scheduler.getTrigger(new TriggerKey((String) keys[0], (String) keys[1]));
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
        return trigger;
    }
    @Override
    public Set<Object> getTriggers() {
        try {
            return new HashSet<Object>(scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup()));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return new HashSet<Object>();
    }
    @Override
    public List<Object> getTasks() {
        try {
            return new ArrayList<Object>(scheduler.getCurrentlyExecutingJobs());
        } catch (SchedulerException e) {
            e.printStackTrace();
            return new ArrayList<Object>();
        }
    }
    @Override
    public String getInfo() {
        StringBuffer buffer = new StringBuffer();
        try {
            for (Object obj : getTriggers()) {
                TriggerKey key = (TriggerKey) obj;
                Trigger trigger = scheduler.getTrigger(key);
                buffer.append(trigger.getKey());
                buffer.append("\t");
                buffer.append(scheduler.getTriggerState(key));
                buffer.append("\t");
                buffer.append(trigger.getPreviousFireTime());
                buffer.append("\t");
                buffer.append(trigger.getNextFireTime());
                buffer.append("\n");
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}

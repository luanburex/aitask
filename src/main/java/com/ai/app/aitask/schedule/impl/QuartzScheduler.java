package com.ai.app.aitask.schedule.impl;

import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.JobListener;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.impl.StdSchedulerFactory;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.schedule.ITaskScheduler;
import com.ai.app.aitask.task.builder.ITaskBuilder;

public class QuartzScheduler implements ITaskScheduler {
    protected final static Logger log = Logger.getLogger(QuartzScheduler.class);
    private Scheduler             scheduler;

    public QuartzScheduler() throws SchedulerException {
        Config config = Config.instance("quartz.properties");
        scheduler = new StdSchedulerFactory(config.getProperties(null)).getScheduler();
        scheduler.standby();
        ListenerManager man = scheduler.getListenerManager();
        for (Entry<Object, Object> entry : config.getProperties("listeners").entrySet()) {
            try {
                Class<?> sub = Class.forName((String) entry.getKey());
                Class<?> sup = Class.forName((String) entry.getValue());
                if (SchedulerListener.class == sup) {
                    man.addSchedulerListener(sub.asSubclass(SchedulerListener.class).newInstance());
                } else if (JobListener.class == sup) {
                    man.addJobListener(sub.asSubclass(JobListener.class).newInstance());
                }
            } catch (ClassNotFoundException e) {
                log.error("Listener not found : " + entry.getValue());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        try {
            scheduler.start();
            log.info("Quartz daemon start");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        try {
            scheduler.shutdown();
            log.info("Quartz daemon shutdown");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTask(ITaskBuilder taskBuilder, boolean replace) {

    }

    @Override
    public Object getTaskState(Object... keys) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void suspendTask(Object... keys) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fireTask(Object... keys) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<Object> getTriggers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getTasks() {
        // TODO Auto-generated method stub
        return null;
    }
}

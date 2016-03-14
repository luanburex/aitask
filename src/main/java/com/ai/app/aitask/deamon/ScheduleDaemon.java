package com.ai.app.aitask.deamon;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.schedule.TaskSchedule;

public class ScheduleDaemon {

    private final static transient Logger log           = Logger.getLogger(ScheduleDaemon.class);

    /** The Base Scheduler of Quartz */
    private Scheduler                     scheduler     = null;
    /** The Schduler of Task */
    private TaskSchedule                  taskscheduler = null;
    private static ScheduleDaemon         singleton     = null;

    /** return the Scheduler of Task */
    public TaskSchedule getTaskSchedule() {
        return taskscheduler;
    }
    /**
     * the private singleton Constructor of ScheduleDaemon
     */
    private ScheduleDaemon() {
    }

    /**
     * return the singleton ScheduleDaemon Object
     *
     * @return
     */
    public static ScheduleDaemon instance() {
        if (null == singleton) {
            return singleton = new ScheduleDaemon();
        } else {
            return singleton;
        }
    }

    /**
     * start the Quartz Daemon
     */
    public void start() {
        Config config = Config.instance("agent.properties");
        try {
            scheduler = new StdSchedulerFactory(config.getProperties(null)).getScheduler();

            scheduler.standby();
            this.taskscheduler = new TaskSchedule(scheduler);
            log.info("Quartz daemon start.");
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * shutdown the Quartz Daemon
     *
     * @throws SchedulerException
     */
    public void shutdown() throws SchedulerException {
        scheduler.shutdown();
        log.info("Quartz daemon stop.");
    }

    /**
     * return Quartz Schdule Name.
     *
     * @return
     * @throws SchedulerException
     */
    public String getScheName() throws SchedulerException {
        return scheduler.getSchedulerName();
    }

}

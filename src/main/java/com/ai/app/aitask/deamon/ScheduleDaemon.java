package com.ai.app.aitask.deamon;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.ai.app.aitask.config.AgentProperties;
import com.ai.app.aitask.schedule.TaskSchedule;

public class ScheduleDaemon {

	private final static transient Logger log = Logger.getLogger(ScheduleDaemon.class);
	
	/**
	 * The Base Scheduler of Quartz.
	 */
	private Scheduler scheduler = null;
	/**
	 * The Schduler of Task
	 */
	private TaskSchedule taskscheduler = null;
	
	
	private static ScheduleDaemon singletonObj = null;

	
	/**
	 * return the Schduler of Task
	 * @return
	 */
	public TaskSchedule getTaskSchedule(){
		return taskscheduler;
	}
	/**
	 * the private singleton Constructor of ScheduleDaemon
	 * 
	 * @throws SchedulerException
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 */
	private ScheduleDaemon() throws SchedulerException, IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		this.start();
	}

	/**
	 * return the singleton ScheduleDaemon Object
	 * 
	 * @return
	 * @throws SchedulerException
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 */
	public static ScheduleDaemon instance() throws SchedulerException, IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (singletonObj == null)
			singletonObj = new ScheduleDaemon();
		return singletonObj;
	}

	/**
	 * start the Quartz Daemon
	 * 
	 * @throws SchedulerException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 */
	public void start() throws SchedulerException,IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		StdSchedulerFactory factory = new StdSchedulerFactory(AgentProperties.getInstance());
		scheduler = factory.getScheduler();
		
		scheduler.standby();
		this.taskscheduler = new TaskSchedule(scheduler);
		log.info("Quartz daemon start.");

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
	 * @return
	 * @throws SchedulerException 
	 */
	public String getScheName() throws SchedulerException {
		return scheduler.getSchedulerName();
	}

	

}

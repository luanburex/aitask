package com.ai.app.aitask.deamon;

import java.util.Timer;
import java.util.TimerTask;

public class TaskSyncDaemon {
	
	private static TaskSyncDaemon singletonObj = null;
	private Timer timer = null;
	
	private TaskSyncDaemon(){
		 timer = new Timer();
		 timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("run");
				
			}
		}, 100l, 60*1000l);
	}
	
	synchronized public static TaskSyncDaemon instance(){
		if (singletonObj == null)
			singletonObj = new TaskSyncDaemon();
		return singletonObj;
	}
}

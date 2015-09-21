package com.ai.app.aitask.utils;

import org.quartz.SchedulerException;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

import com.ai.app.aitask.schedule.TaskSchedule;

public class TriggerStateWaitUnil {

	public static boolean waitStateUntil(TaskSchedule s, TriggerKey triggerKey, TriggerState state, long wait_time) throws InterruptedException, SchedulerException{
		
		long _wait = 0l;
		while(_wait < wait_time){
			if(state.equals(s.getTaskState(triggerKey)))
					return true;
			Thread.sleep(100l);
			_wait += 100l;
		}
		return false;
	}
}

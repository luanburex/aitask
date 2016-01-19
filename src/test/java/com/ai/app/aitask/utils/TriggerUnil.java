package com.ai.app.aitask.utils;

import org.quartz.SchedulerException;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

import com.ai.app.aitask.schedule.TaskSchedule;

public class TriggerUnil {

    public static boolean waitStateUntil(TaskSchedule s, TriggerKey triggerKey, TriggerState state,
            long wait_time) throws InterruptedException, SchedulerException {
        boolean result = false;
        long _wait = 0l;
        while (_wait < wait_time) {
            if (state.equals(s.getTaskState(triggerKey))) {
                result = true;
                break;
            }
            Thread.sleep(100l);
            _wait += 100l;
        }
        System.out.println("actual:"+s.getTaskState(triggerKey));
        System.out.println("expect:"+state);
        System.err.println("result:"+result+" dur:"+_wait);
        return result;
    }

    public static boolean waitPreviousTimeNull(TaskSchedule s, TriggerKey triggerKey, long wait_time)
            throws SchedulerException, InterruptedException {

        long _wait = 0l;
        while (_wait < wait_time) {
            if (s.getScheduler().getTrigger(triggerKey).getPreviousFireTime() == null)
                return true;
            Thread.sleep(100l);
            _wait += 100l;
        }
        return false;
    }
}

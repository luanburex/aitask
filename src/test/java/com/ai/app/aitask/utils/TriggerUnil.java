package com.ai.app.aitask.utils;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

import com.ai.app.aitask.schedule.ITaskScheduler;
import com.ai.app.aitask.task.execute.ExecuteTest;

public class TriggerUnil {
    final static protected Logger log = Logger.getLogger(ExecuteTest.class);

    public static boolean waitStateUntil(ITaskScheduler s, TriggerKey triggerKey, TriggerState state,
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
        log.info("actual:" + s.getTaskState(triggerKey));
        log.info("expect:" + state);
        log.info("result:" + result + " dur:" + _wait);
        return result;
    }

    public static boolean waitPreviousTimeNull(ITaskScheduler s, TriggerKey triggerKey, long wait_time)
            throws SchedulerException, InterruptedException {

        long _wait = 0l;
        while (_wait < wait_time) {
//            if (s.getScheduler().getTrigger(triggerKey).getPreviousFireTime() == null) {
//                return true;
//            }
            Thread.sleep(100l);
            _wait += 100l;
        }
        return false;
    }
}

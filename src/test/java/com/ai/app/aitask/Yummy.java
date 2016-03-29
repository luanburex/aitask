package com.ai.app.aitask;

import java.util.Timer;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDataMap;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class Yummy {

    public static void main(String[] args) throws Exception {
        Timer timer = new Timer();
        Thread.sleep(1000);
        timer.cancel();
    }
}

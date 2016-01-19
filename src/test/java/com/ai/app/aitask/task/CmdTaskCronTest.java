package com.ai.app.aitask.task;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.quartz.Trigger.TriggerState;

import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.task.builder.ITaskBuilder;
import com.ai.app.aitask.utils.FileUtils;
import com.ai.app.aitask.utils.TriggerUnil;

public class CmdTaskCronTest {
    final static private Logger log = Logger.getLogger(CmdTaskCronTest.class);

    public static ScheduleDaemon sd;
    
    @BeforeClass
    public static void start() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SchedulerException, IOException, SQLException{
        sd = ScheduleDaemon.instance();
    }
    
    @Test
    public void testCronRun() throws Exception{
        String xml_file = Thread.currentThread().getContextClassLoader().getResource("").getPath().toString() 
                + "/com/ai/app/aitask/task/cmd_script_task005_cron.xml";
        String xml_str = FileUtils.readFileToString(xml_file);
        ITaskBuilder ts = TaskDirector.getCmdTaskBuilder(xml_str);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis() + 2000l));
        String t = c.get(Calendar.SECOND) + " " + c.get(Calendar.MINUTE) + " * * * ?";
        log.info(t);
        ts.getDatamap().put("cron", t);
        ts.generate();
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(), 
                ts.getTrigger().getKey(), TriggerState.NONE, 1000l));
        
        sd.getTaskSchedule().addTask(ts, true);
        
        log.info(sd.getTaskSchedule().getTaskState(ts.getTrigger().getKey()));
        
        log.info(sd.getTaskSchedule().getScheduler().getTrigger(ts.getTrigger().getKey()).getPreviousFireTime());
        log.info(sd.getTaskSchedule().getScheduler().getTrigger(ts.getTrigger().getKey()).getNextFireTime());
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(), 
                ts.getTrigger().getKey(), TriggerState.NORMAL, 2000l));
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(), 
                ts.getTrigger().getKey(), TriggerState.BLOCKED, 5000l));
        log.info(sd.getTaskSchedule().getTaskState(ts.getTrigger().getKey()));
        log.info(sd.getTaskSchedule().getScheduler().getTrigger(ts.getTrigger().getKey()).getPreviousFireTime());
        log.info(sd.getTaskSchedule().getScheduler().getTrigger(ts.getTrigger().getKey()).getNextFireTime());
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(), 
                ts.getTrigger().getKey(), TriggerState.NORMAL, 5000l));

    }
}

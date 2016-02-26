package com.ai.app.aitask.task;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.quartz.Trigger.TriggerState;

import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.common.Mapper;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.task.builder.ITaskBuilder;
import com.ai.app.aitask.utils.FileUtils;
import com.ai.app.aitask.utils.TriggerUnil;

public class CmdTaskTest implements Constants {
    final static protected Logger log = Logger.getLogger(CmdTaskTest.class);

    public static ScheduleDaemon  sd;

    @BeforeClass
    public static void start() throws InstantiationException, IllegalAccessException,
    ClassNotFoundException, SchedulerException, IOException, SQLException {
        sd = ScheduleDaemon.instance();
    }

    @Test
    public void testSimpleRun() throws Exception {
        String xml_file = Thread.currentThread().getContextClassLoader().getResource("").getPath()
                .toString()
                + "/com/ai/app/aitask/task/cmd_script_task001.xml";
        String xml_str = FileUtils.readFileToString(xml_file);
        ITaskBuilder ts = TaskDirector.getBuilder(Mapper.parseXML(xml_str),
                Integer.toString(TASK_TYPE_CMD));
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(),
                ts.getTrigger().getKey(), TriggerState.NONE, 1000l));
        sd.getTaskSchedule().addTask(ts, true);
        log.info(sd.getTaskSchedule().getTaskState(ts.getTrigger().getKey()));
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(),
                ts.getTrigger().getKey(), TriggerState.BLOCKED, 1000l));
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(),
                ts.getTrigger().getKey(), TriggerState.NONE, 5000l));
    }

    @Test
    public void testSerialRun() throws Exception {
        String xml_file1 = Thread.currentThread().getContextClassLoader().getResource("").getPath()
                .toString()
                + "/com/ai/app/aitask/task/cmd_script_task001.xml";
        String xml_str1 = FileUtils.readFileToString(xml_file1);
        ITaskBuilder ts1 = TaskDirector.getBuilder(null, Integer.toString(TASK_TYPE_CMD));

        String xml_file2 = Thread.currentThread().getContextClassLoader().getResource("").getPath()
                .toString()
                + "/com/ai/app/aitask/task/cmd_script_task001.xml";
        String xml_str2 = FileUtils.readFileToString(xml_file2);
        ITaskBuilder ts2 = TaskDirector.getBuilder(null, Integer.toString(TASK_TYPE_CMD));
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(), ts1.getTrigger()
                .getKey(), TriggerState.NONE, 1000l));
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(), ts2.getTrigger()
                .getKey(), TriggerState.NONE, 1000l));

        sd.getTaskSchedule().addTask(ts1, true);
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(), ts1.getTrigger()
                .getKey(), TriggerState.BLOCKED, 1000l));

        sd.getTaskSchedule().addTask(ts2, true);
        Thread.sleep(200l);
        Assert.assertEquals(1, sd.getTaskSchedule().getScheduler().getCurrentlyExecutingJobs()
                .size());
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(), ts2.getTrigger()
                .getKey(), TriggerState.BLOCKED, 5000l));
        Assert.assertEquals(1, sd.getTaskSchedule().getScheduler().getCurrentlyExecutingJobs()
                .size());

        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(), ts1.getTrigger()
                .getKey(), TriggerState.NONE, 5000l));
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(), ts2.getTrigger()
                .getKey(), TriggerState.NONE, 5000l));
    }

    @Test
    public void testInterruptSerialRun() throws Exception {
        //        ITaskBuilder ts = TaskDirector.getBuilder(FileUtils.readFileInClasspath(
        //                "/com/ai/app/aitask/task/cmd_script_task003_longtime.xml"));
        ITaskBuilder ts = TaskDirector.getBuilder(null, Integer.toString(TASK_TYPE_CMD));
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(),
                ts.getTrigger().getKey(), TriggerState.NONE, 1000l));
        sd.getTaskSchedule().addTask(ts, true);
        Thread.sleep(500l);
        sd.getTaskSchedule().interruptByTrigger(ts.getTrigger().getKey());
        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getTaskSchedule(),
                ts.getTrigger().getKey(), TriggerState.NONE, 1000l));
    }
}

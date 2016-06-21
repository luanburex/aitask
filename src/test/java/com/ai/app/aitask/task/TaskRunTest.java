package com.ai.app.aitask.task;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.SchedulerException;
import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.utils.FileUtils;

public class TaskRunTest implements Constants {
    static final protected Logger logger = Logger.getLogger(TaskRunTest.class);

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
//        ITaskBuilder ts = TaskBuilderFactory.getBuilder(Mapper.parseXML(xml_str),
//                Integer.toString(TASK_TYPE_CMD));
//
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(),
//                ts.getAuth().mapKey(), TriggerState.NONE, 1000l));
//        sd.getScheduler().addTask(ts, true);
//        logger.info(sd.getScheduler().getTaskState(ts.getAuth().mapKey()));
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(),
//                ts.getAuth().mapKey(), TriggerState.BLOCKED, 1000l));
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(),
//                ts.getAuth().mapKey(), TriggerState.NONE, 5000l));
    }

    @Test
    public void testSerialRun() throws Exception {
        String xml_file1 = Thread.currentThread().getContextClassLoader().getResource("").getPath()
                .toString()
                + "/com/ai/app/aitask/task/cmd_script_task001.xml";
        String xml_str1 = FileUtils.readFileToString(xml_file1);
//        ITaskBuilder ts1 = TaskBuilderFactory.getBuilder(null, Integer.toString(TASK_TYPE_CMD));
//
//        String xml_file2 = Thread.currentThread().getContextClassLoader().getResource("").getPath()
//                .toString()
//                + "/com/ai/app/aitask/task/cmd_script_task001.xml";
//        String xml_str2 = FileUtils.readFileToString(xml_file2);
//        ITaskBuilder ts2 = TaskBuilderFactory.getBuilder(null, Integer.toString(TASK_TYPE_CMD));
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(), ts1.getAuth()
//                .mapKey(), TriggerState.NONE, 1000l));
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(), ts2.getAuth()
//                .mapKey(), TriggerState.NONE, 1000l));
//
//        sd.getScheduler().addTask(ts1, true);
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(), ts1.getAuth()
//                .mapKey(), TriggerState.BLOCKED, 1000l));
//
//        sd.getScheduler().addTask(ts2, true);
//        Thread.sleep(200l);
//        Assert.assertEquals(1, sd.getScheduler().getScheduler().getCurrentlyExecutingJobs()
//                .size());
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(), ts2.getAuth()
//                .mapKey(), TriggerState.BLOCKED, 5000l));
//        Assert.assertEquals(1, sd.getScheduler().getScheduler().getCurrentlyExecutingJobs()
//                .size());
//
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(), ts1.getAuth()
//                .mapKey(), TriggerState.NONE, 5000l));
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(), ts2.getAuth()
//                .mapKey(), TriggerState.NONE, 5000l));
    }

    @Test
    public void testInterruptSerialRun() throws Exception {
        //        ITaskBuilder ts = TaskDirector.getBuilder(FileUtils.readFileInClasspath(
        //                "/com/ai/app/aitask/task/cmd_script_task003_longtime.xml"));
//        ITaskBuilder ts = TaskBuilderFactory.getBuilder(null, Integer.toString(TASK_TYPE_CMD));
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(),
//                ts.getAuth().mapKey(), TriggerState.NONE, 1000l));
//        sd.getScheduler().addTask(ts, true);
//        Thread.sleep(500l);
//        sd.getScheduler().interruptByTrigger(ts.getAuth().mapKey());
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(),
//                ts.getAuth().mapKey(), TriggerState.NONE, 1000l));
    }
}

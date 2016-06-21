package com.ai.app.aitask.task;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.SchedulerException;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.utils.FileUtils;

public class TaskCronTest {
    static final protected Logger logger = Logger.getLogger(TaskCronTest.class);

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
        //        ITaskBuilder ts = TaskDirector.getCmdTaskBuilder(xml_str);
//        ITaskBuilder ts = TaskBuilderFactory.getBuilder(null, Integer.toString(Constants.TASK_TYPE_CMD));

        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis() + 2000l));
        String t = c.get(Calendar.SECOND) + " " + c.get(Calendar.MINUTE) + " * * * ?";
        logger.info(t);
        //        ts.getDatamap().put("cron", t);
//        ts.getContent().getJobDataMap().put("cron", t);
//        ts.build();
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(),
//                ts.getAuth().mapKey(), TriggerState.NONE, 1000l));
//
//        sd.getScheduler().addTask(ts, true);
//
//        logger.info(sd.getScheduler().getTaskState(ts.getAuth().mapKey()));
//
//        logger.info(sd.getScheduler().getScheduler().getTrigger(ts.getAuth().mapKey()).getPreviousFireTime());
//        logger.info(sd.getScheduler().getScheduler().getTrigger(ts.getAuth().mapKey()).getNextFireTime());
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(),
//                ts.getAuth().mapKey(), TriggerState.NORMAL, 2000l));
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(),
//                ts.getAuth().mapKey(), TriggerState.BLOCKED, 5000l));
//        logger.info(sd.getScheduler().getTaskState(ts.getAuth().mapKey()));
//        logger.info(sd.getScheduler().getScheduler().getTrigger(ts.getAuth().mapKey()).getPreviousFireTime());
//        logger.info(sd.getScheduler().getScheduler().getTrigger(ts.getAuth().mapKey()).getNextFireTime());
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(),
//                ts.getAuth().mapKey(), TriggerState.NORMAL, 5000l));

    }
}

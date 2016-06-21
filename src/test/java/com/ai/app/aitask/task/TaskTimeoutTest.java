package com.ai.app.aitask.task;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.SchedulerException;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.utils.FileUtils;

public class TaskTimeoutTest {
    static final protected Logger logger = Logger.getLogger(TaskRunTest.class);

    public static ScheduleDaemon sd;

    @BeforeClass
    public static void start() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SchedulerException, IOException, SQLException{
        sd = ScheduleDaemon.instance();
    }

    @Test
    public void testTimeout() throws Exception, IllegalAccessException, ClassNotFoundException, SchedulerException, IOException, SQLException{
        ScheduleDaemon sd = ScheduleDaemon.instance();
        String xml_file = Thread.currentThread().getContextClassLoader().getResource("").getPath().toString()
                + "/com/ai/app/aitask/task/cmd_script_task004_timeout.xml";
        String xml_str = FileUtils.readFileToString(xml_file);
//        ITaskBuilder ts = TaskBuilderFactory.getBuilder(null, Integer.toString(Constants.TASK_TYPE_CMD));
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(),
//                ts.getAuth().mapKey(), TriggerState.NONE, 1000l));
//        sd.getScheduler().addTask(ts, true);
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(),
//                ts.getAuth().mapKey(), TriggerState.BLOCKED, 1000l));
//        Assert.assertTrue(TriggerUnil.waitStateUntil(sd.getScheduler(),
//                ts.getAuth().mapKey(), TriggerState.NONE, 1000l));
    }
}

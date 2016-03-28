package com.ai.app.aitask.task.builder.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.FileUtil;
import com.ai.app.aitask.exception.TaskParseException;
import com.ai.app.aitask.task.AbstractTask;
import com.ai.app.aitask.task.builder.AbstractTaskBuilder;
import com.ai.app.aitask.task.excutor.IExecutor;
import com.ai.app.aitask.task.excutor.impl.DefaultTaskExecutor;
import com.ai.app.aitask.task.preparer.IDataPreparer;
import com.ai.app.aitask.task.result.IResultFetcher;
import com.ai.app.aitask.task.result.impl.DefaultResultFetcher;

public class CmdTaskBuilder extends AbstractTaskBuilder {

    protected final static Logger log = Logger.getLogger(CmdTaskBuilder.class);
    protected Trigger                       trigger    = null;
    protected JobDetail                     jobDetail  = null;
    protected JobDataMap                    jobDatamap = new JobDataMap();

    protected IExecutor                     executor   = null;
    protected IDataPreparer                 preparer   = null;
    protected IResultFetcher                result     = null;

    @Override
    public void parse(Map<String, Object> datamap) throws Exception {
        // 生成任务执行所需要的文件
        //        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(datamap));

        /*
         * script
         * data file
         *
         * result
         * log
         */
        // 执行的数据文件
        String datapath = "exedata";
        Config config = Config.instance("exedata");

        // The log file
        String logpath = "logpath.txt";
        FileUtil.makeFile(logpath, "这里放日志");
        config.setProperty("task", "log", logpath);

        // The result file
        String resultpath = "resultpath.txt";
        FileUtil.makeFile(resultpath, "这里放结果");
        config.setProperty("task", "result", resultpath);

        Map<String, String> scriptmap = Caster.cast(datamap.get("script"));
        // The script file
        String scriptpath = "script.txt";
        FileUtil.makeFile(scriptpath, scriptmap.get("script"));
        config.setProperty("task", "script", scriptpath);

        Map<String, String> taskmap = Caster.cast(datamap.get("task"));
        for (Entry<String, String> entry : taskmap.entrySet()) {
            config.setProperty("task", entry.getKey(), entry.getValue());
        }

        Map<String, Object> detailmap = Caster.cast(datamap.get("data"));
        List<Map<String, String>> detaillist = Caster.cast(detailmap.get("detail"));
        for (int i = 0, sum = detaillist.size(); i < sum; i++) {
            for (Entry<String, String> entry : detaillist.get(i).entrySet()) {
                config.setProperty("detail" + i, entry.getKey(), entry.getValue());
            }
        }
        config.setProperty("task", "detail", Integer.toString(detaillist.size()));

        config.write();

        jobDatamap.put("preparer", null);
        String[] cmd_str = { "ping", "www.baidu.com" };
        // TODO 这里怎么执行先不管了，继续看Executor里的东西
        //        cmd_str = new String[]{"ruby", "aitask.rb", new File(datapath).getAbsolutePath()};
        jobDatamap.put("executor", new DefaultTaskExecutor(cmd_str));
        jobDatamap.put("result", new DefaultResultFetcher(resultpath, logpath));
        
        { // super method
         // check
            Map<String, Object> taskData = Caster.cast(datamap.get("task"));
            if (taskData.get("task_id") == null) {
                throw new TaskParseException("id");
            }
            if (taskData.get("instant") == null) {
                throw new TaskParseException("instant");
            }
            if (taskData.get("cron") == null) {
                throw new TaskParseException("cron");
            }
            jobDatamap.put("timeout", Long.parseLong((String) taskData.get("timeout")));
            jobDatamap.put("datamap", datamap);
        }
        super.parse(datamap);
    }

    @Override
    public void build() {
        Map<String, Map<String, Object>> datamap = Caster.cast(jobDatamap.get("datamap"));
        Map<String, Object> task = datamap.get("task");
        String task_id = (String) task.get("task_id");
        String plan_id = (String) task.get("plan_id");
        Map<String, Object> data = Caster.cast(datamap.get("data"));
        String data_id = (String) data.get("data_id");
        //        String task_category = (String) task.get("task_category");
        String task_group = (String) task.get("task_group");
        String instant = (String) task.get("instant");
        String cron = (String) task.get("cron");
        String keystr = plan_id + "." + task_id + "." + data_id;
        JobKey jobKey = new JobKey(keystr, task_group);

        JobBuilder builder = JobBuilder.newJob(AbstractTask.class);
        this.jobDetail = builder.withIdentity(jobKey).storeDurably().build();

        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        triggerBuilder.withIdentity(keystr, task_group);
        triggerBuilder.usingJobData(jobDatamap);
        if ("1".equals(instant)) {
            triggerBuilder.withSchedule(SimpleScheduleBuilder.simpleSchedule());
        } else {
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
        }
        this.trigger = triggerBuilder.build();trigger.getJobKey()J

        log.debug("generate task key:" + jobDetail.getKey() + "\t trigger:" + trigger.getKey());
    }
}

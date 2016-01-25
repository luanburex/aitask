package com.ai.app.aitask.task.builder;

import java.util.Map;

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
import com.ai.app.aitask.exception.TaskParseException;
import com.ai.app.aitask.task.SerialTask;

public abstract class AbstractTaskBuilder implements ITaskBuilder {

    transient final static protected Logger log        = Logger.getLogger(AbstractTaskBuilder.class);

    protected JobDetail                     jobDetail  = null;
    protected Trigger                       trigger    = null;
    protected JobDataMap                    jobDatamap = new JobDataMap();

    //    protected IExecutor                   executor   = null;
    //    protected IDataPreparer               preparer   = null;
    //    protected IResultFetcher              result     = null;

    //    @Override
    //    public JobDataMap getDatamap() {
    //        return jobDatamap;
    //    }
    //    @Override
    //    public void setDatamap(JobDataMap datamap) {
    //        this.jobDatamap = datamap;
    //    }
    @Override
    public JobDetail getJobDetail() {
        return jobDetail;
    }
    public void setJobDetail(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }
    @Override
    public Trigger getTrigger() {
        return trigger;
    }
    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }
    //    public IExecutor getExecutor() {
    //        return executor;
    //    }
    //    public void setExecutor(IExecutor executor) {
    //        this.executor = executor;
    //    }
    //    public IDataPreparer getPreparer() {
    //        return preparer;
    //    }
    //    public void setPreparer(IDataPreparer preparer) {
    //        this.preparer = preparer;
    //    }
    //    public IResultFetcher getResult() {
    //        return result;
    //    }
    //    public void setResult(IResultFetcher result) {
    //        this.result = result;
    //    }

    @Override
    public void parseTask(Map<String, Object> datamap) throws Exception {
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

    @Override
    public void build() {
        Map<String, Map<String, Object>> datamap = Caster.cast(jobDatamap.get("datamap"));
        Map<String, Object> task = datamap.get("task");
        String task_id = (String) task.get("task_id");
        String task_category = (String) task.get("task_category");
        String task_group = (String) task.get("task_group");
        String instant = (String) task.get("instant");
        String cron = (String) task.get("cron");
        JobKey jobKey = new JobKey(task_category, task_group);

        JobBuilder builder = JobBuilder.newJob(SerialTask.class);
        this.jobDetail = builder.withIdentity(jobKey).storeDurably().build();

        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        triggerBuilder.withIdentity(task_id, task_group);
        triggerBuilder.usingJobData(jobDatamap);
        if ("1".equals(instant)) {
            triggerBuilder.withSchedule(SimpleScheduleBuilder.simpleSchedule());
        } else {
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
        }
        this.trigger = triggerBuilder.build();

        log.debug("generate task key:" + jobDetail.getKey() + "\t trigger:" + trigger.getKey());
    }
}

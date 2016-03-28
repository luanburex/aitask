package com.ai.app.aitask.listener.quartz;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.ai.app.aitask.task.ITask;

public class ExecuteListener implements JobListener {

    protected final static Logger log = Logger.getLogger(ExecuteListener.class);

    @Override
    public String getName() {
        return "TaskExecuteListener";
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        ITask task = (ITask) context.getJobInstance();
//        task.before(context);

        JobDataMap data_map = context.getTrigger().getJobDataMap();
        StringBuilder sb = new StringBuilder();
        for (Object key : data_map.keySet()) {
            sb.append("[").append(key).append("]:").append(data_map.get(key)).append("\n");
        }
        log.info("[" + context.getTrigger().getKey().toString() + "]Datas:\n" + sb.toString());
        log.info("[" + context.getTrigger().getKey().toString() + "]"
                + "run the task BEFORE function.");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException error) {
        ITask task = (ITask) context.getJobInstance();
//        task.after(context, error);

        JobDataMap data_map = context.getTrigger().getJobDataMap();
        StringBuilder sb = new StringBuilder();
        for (Object key : data_map.keySet()) {
            sb.append("[").append(key).append("]:").append(data_map.get(key)).append("\n");
        }
        log.info("[" + context.getTrigger().getKey().toString() + "]Datas:\n" + sb.toString());
        log.info("[" + context.getTrigger().getKey().toString() + "]"
                + "run the task AFTER function.");
    }
}
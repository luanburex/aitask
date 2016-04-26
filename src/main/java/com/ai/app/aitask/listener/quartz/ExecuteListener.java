package com.ai.app.aitask.listener.quartz;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * @author renzq
 */
public class ExecuteListener implements JobListener {

    protected static final Logger logger = Logger.getLogger(ExecuteListener.class);

    @Override
    public String getName() {
        return "TaskExecuteListener";
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        JobDataMap data_map = context.getTrigger().getJobDataMap();
        StringBuilder sb = new StringBuilder();
        for (Object key : data_map.keySet()) {
            sb.append("[").append(key).append("]:").append(data_map.get(key)).append("\n");
        }
        logger.info("[" + context.getTrigger().getKey().toString() + "]Datas:\n" + sb.toString());
        logger.info("[" + context.getTrigger().getKey().toString() + "]"
                + "run the task BEFORE function.");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException error) {
        // TODO handle the error
        //                task.after(context, error);

        JobDataMap data_map = context.getTrigger().getJobDataMap();
        StringBuilder sb = new StringBuilder();
        for (Object key : data_map.keySet()) {
            sb.append("[").append(key).append("]:").append(data_map.get(key)).append("\n");
        }
        logger.info("[" + context.getTrigger().getKey().toString() + "]Datas:\n" + sb.toString());
        logger.info("[" + context.getTrigger().getKey().toString() + "]"
                + "run the task AFTER function.");
    }
}

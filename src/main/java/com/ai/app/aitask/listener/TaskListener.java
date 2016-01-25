package com.ai.app.aitask.listener;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class TaskListener implements JobListener {

    private final static transient Logger log = Logger
            .getLogger(TaskListener.class);

    public TaskListener() throws SQLException, InstantiationException,
    IllegalAccessException, ClassNotFoundException, IOException {
    }

    @Override
    public String getName() {
        return "TaskListener";
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {

    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {

        try {
            Method m = context.getJobInstance().getClass().getMethod("before",JobExecutionContext.class);
            m.invoke(context.getJobInstance(),context);

            JobDataMap data_map = context.getTrigger().getJobDataMap();
            StringBuilder sb = new StringBuilder();
            for(Object key: data_map.keySet()){
                sb.append("[").append(key).append("]:").append(data_map.get(key)).append("\n");
            }
            log.info("["+context.getTrigger().getKey().toString()+"]Datas:\n" + sb.toString());
            log.info("["+context.getTrigger().getKey().toString()+"]"+"run the task BEFORE function.");
        } catch (Exception e) {
            log.error("["+context.getTrigger().getKey().toString()+"]"+"run BEFORE function,error: ",e);
        }
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context,
            JobExecutionException error) {
        try {
            Method m = context.getJobInstance().getClass().getMethod("after",JobExecutionContext.class,JobExecutionException.class);
            m.invoke(context.getJobInstance(),context,error);

            JobDataMap data_map = context.getTrigger().getJobDataMap();
            StringBuilder sb = new StringBuilder();
            for(Object key: data_map.keySet()){
                sb.append("[").append(key).append("]:").append(data_map.get(key)).append("\n");
            }
            log.info("["+context.getTrigger().getKey().toString()+"]Datas:\n" + sb.toString());
            log.info("["+context.getTrigger().getKey().toString()+"]"+"run the task AFTER function.");
        } catch (Exception e) {
            log.error("["+context.getTrigger().getKey().toString()+"]"+"run AFTER function,error: ",e);
        }
    }

}

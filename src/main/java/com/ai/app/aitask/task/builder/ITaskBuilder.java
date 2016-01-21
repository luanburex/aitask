package com.ai.app.aitask.task.builder;

import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import com.ai.app.aitask.common.Constants;

public interface ITaskBuilder extends Constants {

    public Trigger getTrigger();

    public JobDetail getJobDetail();

    //    public JobDataMap getDatamap();

    //    public void setDatamap(JobDataMap datamap);

    public void build() throws Exception;

    public void parseTask(Map<String, Object> datamap) throws Exception;
}

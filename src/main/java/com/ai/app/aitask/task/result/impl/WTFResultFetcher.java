package com.ai.app.aitask.task.result.impl;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.task.result.IResultFetcher;

public class WTFResultFetcher implements IResultFetcher{

    @Override
    public String fetch(JobExecutionContext context) throws JobExecutionException {
        //        Map<String, Map<String, Object>> datamap = Caster.cast(context.getMergedJobDataMap().get("datamap"));

        Map<String, Object> casemap = new HashMap<String, Object>();
        casemap.put("taskId", "1");
        casemap.put("result", "");
        casemap.put("scriptName", "");
        casemap.put("scriptType", "");
        casemap.put("dataDesc", "");
        casemap.put("dataId", "");
        casemap.put("keyEclapse", "");
        casemap.put("endTime", "");
        casemap.put("log", "");
        casemap.put("business", "");
        casemap.put("dataName", "");
        casemap.put("groupNo", "");
        casemap.put("firstExecuteTime", "");
        casemap.put("startTime", "");
        casemap.put("system", "");
        casemap.put("dataTags", "");
        casemap.put("groupName", "");
        casemap.put("scriptId", "");
        casemap.put("executeNum", "");

        Map<String, Object> stepmap = new HashMap<String, Object>();
        stepmap.put("startTime", "");
        stepmap.put("result", "");
        stepmap.put("screenshot", "");
        stepmap.put("stepExpectResult", "");
        stepmap.put("eclapse", "");
        stepmap.put("endTime", "");
        stepmap.put("stepDesc", "");
        stepmap.put("stepId", "");
        stepmap.put("caseLogId", "");
        stepmap.put("log", "");
        stepmap.put("stepName", "");

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("caseLog", casemap);
        result.put("stepLog", new Object[]{stepmap});

        System.err.println(casemap);

        return new com.google.gson.Gson().toJson(result);
    }

    @Override
    public String error(JobExecutionContext context, JobExecutionException exception)
            throws JobExecutionException {
        Map<String, Object> datamap = Caster.cast(context.getMergedJobDataMap().get("datamap"));
        System.out.println(datamap);
        return fetch(context);
    }

}

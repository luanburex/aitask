package com.ai.app.aitask.task.result.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.FileUtil;
import com.ai.app.aitask.task.result.IResultFetcher;

public class TestResultFetcher implements IResultFetcher {

    private String resultpath;
    private String logpath;

    public TestResultFetcher(String resultpath, String logpath) {
        super();
        this.resultpath = resultpath;
        this.logpath = logpath;
    }

    @Override
    public String fetch(JobExecutionContext context) throws JobExecutionException {
        String resultfile;
        String logfile;
        try {
            //读结果文件
            resultfile = FileUtil.readFile(new FileReader(resultpath));
            //读日志文件，可能会做个LogFetcher再，带即时更新功能
            logfile = FileUtil.readFile(new FileReader(logpath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Object dataobj = context.getMergedJobDataMap().getWrappedMap().get("datamap");

        Map<String, Object> datamap = Caster.cast(dataobj);
        Map<String, String> taskmap = Caster.cast(datamap.get("task"));
        datamap = Caster.cast(datamap.get("data"));
        /*
         * {
         * data_id=dataId2,
         * paramClob=,
         * detail_id=detailId1,
         * paramDesc=111,
         * paramValue=111,
         * paramVarible=111,
         * paramName=111,
         * paramType=111
         * }
         */
        List<Object> steplog = new LinkedList<Object>();
        List<Map<String, String>> detaillist = Caster.cast(datamap.get("detail"));
        for(Map<String, String> detail : detaillist) {
            Map<String, Object> stepmap = new HashMap<String, Object>();
            stepmap.put("startTime", "");
            stepmap.put("result", "");
            stepmap.put("screenshot", "");
            stepmap.put("stepExpectResult", "");
            stepmap.put("eclapse", "");
            stepmap.put("endTime", "");
            stepmap.put("stepDesc", "");
            stepmap.put("stepId", detail.get("detail_id"));
            stepmap.put("caseLogId", "");
            stepmap.put("log", "随便写点日志咯");
            stepmap.put("stepName", "");
            steplog.add(stepmap);
        }
        //        Map<String, String> taskmap = datamap.get("task");
        Map<String, Object> casemap = new HashMap<String, Object>();
        casemap.put("taskId", taskmap.get("task_id"));
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

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("caseLog", casemap);
        result.put("stepLog", steplog);

        return new com.google.gson.Gson().toJson(result);
    }

    @Override
    public String error(JobExecutionContext context, JobExecutionException exception)
            throws JobExecutionException {
        return fetch(context);
    }

}

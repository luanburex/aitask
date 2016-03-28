package com.ai.app.aitask.task.result.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.FileUtil;
import com.ai.app.aitask.task.result.IResultFetcher;

public class DefaultResultFetcher implements IResultFetcher {

    private Map<String, Object> exedata;

    public DefaultResultFetcher(Map<String, Object> exedata) {
        this.exedata = exedata;
    }

    @Override
    public Map<String, Object> fetch(Map<String, Object> datamap) {
        String pathWorkspace = (String) exedata.get("pathWorkspace");
        String pathExedata = (String) exedata.get("pathExedata");
        String pathScript = (String) exedata.get("pathScript");
        String pathResult = (String) exedata.get("pathResult");
        String pathLog = (String) exedata.get("pathLog");

        Config exefile = Config.instance(pathExedata);
        /**
         * expected data structure
         * see result_schedule
         */
        List<Object> steplog = new LinkedList<Object>();
        //            List<Map<String, String>> detaillist = Caster.cast(datamap.get("detail"));
        List<Map<String, String>> detaillist = new ArrayList<Map<String, String>>();
        for (Map<String, String> detail : detaillist) {
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
        Map<String, String> taskmap = new HashMap<String, String>();
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

        Map<String, Object> casepair = new HashMap<String, Object>();
        casepair.put("caseLog", casemap);
        casepair.put("stepLog", new Object[] { steplog });
        Object resultlist = new Object[] { casepair }; //<-- the result json? Maybe

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("cslog", resultlist);
        return resultMap;
    }
    @Override
    public Map<String, Object> error(Map<String, Object> datamap, Exception exception) {
        // TODO Auto-generated method stub
        exception.getMessage();
        return null;
    }
}

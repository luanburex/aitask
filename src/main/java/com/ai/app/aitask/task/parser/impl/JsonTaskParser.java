package com.ai.app.aitask.task.parser.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Mapper;
import com.ai.app.aitask.task.parser.ITaskParser;

public class JsonTaskParser implements ITaskParser {
    @Override
    public List<Map<String, Object>> parseContent(String content) {
        Map<String, Object> rawmap = Mapper.parseJSON(content);
        Map<String, Object> planMap = new HashMap<String, Object>();
        Map<String, Object> scriptMap = new HashMap<String, Object>();
        Map<String, List<Object>> dataMap = new HashMap<String, List<Object>>();
        Map<String, Map<String, Map<String, Object>>> detailMap;
        detailMap = new HashMap<String, Map<String, Map<String, Object>>>();
        Map<String, String> taskScriptPair = new HashMap<String, String>();
        PlanMap: {          // Plan 1:1 Task
            List<Map<String, Object>> planList = Caster.cast(rawmap.get("plan"));
            for (Map<String, Object> plan : planList) {
                planMap.put((String) plan.get("planId"), plan);
            }
        }
        ScriptMap: {        // Script 1:1 Task
            List<Map<String, Object>> scriptList = Caster.cast(rawmap.get("scr"));
            for (Map<String, Object> script : scriptList) {
                scriptMap.put((String) script.get("scriptId"), script);
            }
        }
        DetailMap: {        // Detail N:1 Data
            List<Map<String, Object>> detailList = Caster.cast(rawmap.get("taskDataDetail"));
            for (Map<String, Object> detail : detailList) {
                String dataId = (String) detail.get("dataId");
                if (!detailMap.containsKey(dataId)) {
                    detailMap.put(dataId, new HashMap<String, Map<String, Object>>());
                }
                detailMap.get(dataId).put((String) detail.get("datadetailId"), detail);
            }
        }
        DataMap: {          // Data N:1 Task
            List<Map<String, Object>> dataList = Caster.cast(rawmap.get("taskData"));
            for (Map<String, Object> data : dataList) {
                String taskId = (String) data.get("taskId");
                if (!dataMap.containsKey(taskId)) {
                    dataMap.put(taskId, new LinkedList<Object>());
                    detailMap.put(taskId, new HashMap<String, Map<String, Object>>());
                }
                String dataId = (String) data.get("dataId");
                if (!detailMap.get(taskId).containsKey(dataId)) {
                    detailMap.get(taskId).put(dataId, new HashMap<String, Object>());
                }
                dataMap.get(taskId).add(data);
                detailMap.get("taskId").get(dataId).putAll(detailMap.remove(dataId));
            }
        }
        TaskScriptPair: {
            List<Map<String, String>> taskScriptPairList = Caster.cast(rawmap.get("taskScript"));
            for (Map<String, String> tsp : taskScriptPairList) {
                taskScriptPair.put(tsp.get("taskId"), tsp.get("scriptId"));
            }
        }
        List<Map<String, Object>> taskList = new LinkedList<Map<String, Object>>();
        List<Map<String, Object>> tasks = Caster.cast(rawmap.get("task"));
        for (Map<String, Object> task : tasks) {
            Map<String, Object> datamap = new HashMap<String, Object>();
            String taskId = (String) task.get("taskId");
            datamap.put("task", task);
            datamap.put("plan", planMap.get(task.get("planId")));
            datamap.put("script", scriptMap.get(taskScriptPair.get(taskId)));
            datamap.put("datalist", dataMap.get(taskId));
            datamap.put("detaillist", detailMap.get(taskId));
            taskList.add(datamap);
        }
        return taskList;
    }
}

package com.ai.app.aitask.task.builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.common.Mapper;
import com.ai.app.aitask.task.builder.impl.DefaultTaskBuilder;

public class TaskBuilderFactory implements Constants {
    protected final static Logger log = Logger.getLogger(TaskBuilderFactory.class);

    public static List<ITaskBuilder> parseBuilder(String content) {
        Map<String, Object> rawmap = parseRawmap(content);
        List<Map<String, Object>> datamaplist = parseDatamap(rawmap);
        List<ITaskBuilder> builderList = new LinkedList<ITaskBuilder>();
        for (Map<String, Object> datamap : datamaplist) {
            ITaskBuilder builder = new DefaultTaskBuilder();
            builder.parse(datamap);
            builder.build();
            builderList.add(builder);
        }
        return builderList;
    }

    private static Map<String, Object> parseRawmap(String content) {
        Map<String, Object> rawmap = Mapper.parseJSON(content);
        XML: {
            String xml = (String) rawmap.get("xml");
            if (null != xml && !xml.trim().isEmpty()) {
                try {
                    return Mapper.parseXML(xml);
                } catch (Exception e) {
                    log.error("fail parsing XML");
                }
            }
        }
        JSON: {
            return rawmap;
        }
    }

    private static List<Map<String, Object>> parseDatamap(Map<String, Object> rawmap) {
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
    private void parseJSONTask(Map<String, Object> sourceMap) {
        Map<String, String> planDict = new HashMap<String, String>();
        Map<String, String> taskDict = new HashMap<String, String>();
        Map<String, String> scriptDict = new HashMap<String, String>();
        Map<String, String> taskdataDict = new HashMap<String, String>();
        Map<String, String> taskscriptDict = new HashMap<String, String>();
        Map<String, String> taskdatadetailDict = new HashMap<String, String>();

        planDict.put("ifInstance", "instant");

        taskDict.put("planId", "plan_id");
        planDict.put("planId", "plan_id");

        taskDict.put("planName", "plan_name");
        planDict.put("planName", "plan_name");

        taskDict.put("taskId", "task_id");
        taskdataDict.put("taskId", "task_id");
        taskscriptDict.put("taskId", "task_id");

        taskdataDict.put("dataId", "data_id");
        taskdatadetailDict.put("dataId", "data_id");

        taskdatadetailDict.put("datadetailId", "detail_id");

        taskdataDict.put("dataName", "data_name");

        scriptDict.put("scriptId", "script_id");
        taskdataDict.put("scriptId", "script_id");
        taskscriptDict.put("scriptId", "script_id");

        //output        json            xml         desc
        //task_id       task/taskId     ID
        //              taskData/taskId
        //              taskScript/taskId
        //task_name                     name
        //task_category                 ctype
        //task_group                                all AITASK
        //agent_id                      agent_id
        //agent_name                    agent_name
        //group_no                      group_no
        //group_name                    group_name
        //instant       plan/ifInstance instant     true / flase 1/ 0
        //timeout                       timeout
        //cron          plan/cron       cron

        //4bat
        //bat_path                      bat_path
        //ini_path                      ini_path
        //script_path                   script_path
        //project_path                  project_path

        //4cmd
        //cmd_str                       cmd_str

        Map<String, Map<String, String>> dict = new HashMap<String, Map<String, String>>();
        dict.put("plan", planDict);                     // plan : task      1:n
        dict.put("task", taskDict);
        dict.put("scr", scriptDict);                    // task : src       1:1
        dict.put("taskData", taskdataDict);             // task : data      1:1
        dict.put("taskScript", taskscriptDict);
        dict.put("taskDataDetail", taskdatadetailDict); // task : detail    1:

        Mapper.transfer(sourceMap, dict);
    }

    private List<ITaskBuilder> parseXMLTask(Map<String, Object> sourceMap) {
        List<ITaskBuilder> taskList = new LinkedList<ITaskBuilder>();
        try {
            Document document = DocumentHelper.parseText(null);
            Element root = document.getRootElement();
            for (Object t : root.elements("task")) {
                HashMap<String, Object> dataMap = new HashMap<String, Object>();
                Element task_elem = (Element) t;
                dataMap.put("task_id", task_elem.attributeValue("ID"));
                dataMap.put("task_name", task_elem.attributeValue("name", "noname"));
                dataMap.put("task_category", task_elem.attributeValue("category", "none"));
                dataMap.put("task_group", "AITASK");
                dataMap.put("agent_id", task_elem.attributeValue("agent_id", ""));
                dataMap.put("agent_name", task_elem.attributeValue("agent_name", ""));
                dataMap.put("group_no", task_elem.attributeValue("group_no", ""));
                dataMap.put("group_name", task_elem.attributeValue("group_name", ""));
                dataMap.put("instant", task_elem.attributeValue("instant", ""));
                dataMap.put("timeout", Long.valueOf(task_elem.attributeValue("timeout", "-1")));
                dataMap.put("cron", task_elem.attributeValue("cron", ""));
                //                for (Object c : task_elem.elements("case")) {
                //                    Element case_elem = (Element) c;
                //                }
                //                ITaskBuilder task = null;
                try {
                    String task_category = (String) dataMap.get("task_catagory");
                    //                    taskList.add(TaskBuilderFactory.getBuilder(dataMap, task_category));
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("fail parsing xml");
        }
        return taskList;
    }
}

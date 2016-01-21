package com.ai.app.aitask.schedule;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.ContentType;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.common.Mapper;
import com.ai.app.aitask.net.RequestWorker;
import com.ai.app.aitask.task.TaskDirector;
import com.ai.app.aitask.task.builder.ITaskBuilder;

public class TaskFetcher implements Constants {

    private final static Log log              = LogFactory.getLog(TaskFetcher.class);
    private TaskSchedule     schedule         = null;
    private TaskSyncRunable  task_sync        = null;
    private Thread           task_sync_thread = null;
    private Config           config;

    public TaskFetcher(TaskSchedule ts, long sync_task_interval_time) {
        this.schedule = ts;
        this.config = Config.instance("client.properties");
        ;
        this.task_sync = new TaskSyncRunable(this.schedule);
        this.task_sync.setIntervalTime(sync_task_interval_time);
        this.task_sync_thread = new Thread(this.task_sync);
        this.task_sync_thread.start();
    }

    public void fetch() {
        String url = config.getProperty(null, "aitask.sync.url");
        String agent_name = config.getProperty(null, "aitask.name");
        HashMap<String, String> queryPairs = new HashMap<String, String>();
        queryPairs.put("agent_name", agent_name);

        RequestWorker worker = new RequestWorker(url, null);                                        // 取任务
        try {
            worker.Post(RequestWorker.formEntity(queryPairs), ContentType.APPLICATION_JSON);
        } catch (ConnectException e1) {
            // e1.printStackTrace();
            log.error("req : failed connecting");
            return;
        } finally {
            log.info("resp msg:" + worker.getResponseMessage());
            log.info("resp code:" + worker.getResponseContent());
        }

        String content = worker.getResponseContent();
        // LinkedList<ITaskBuilder> taskList = new LinkedList<ITaskBuilder>();
        List<ITaskBuilder> taskList = null;
        if (null == content || content.trim().isEmpty()) {
            log.error("no effective content");
            return;
        } else {
            Map<String, Object> sourceMap = Mapper.parseJSON(content);
            if (sourceMap.containsKey("xml") && !((String) sourceMap.get("xml")).trim().isEmpty()) {
                try {
                    taskList = parseXMLTask(Mapper.parseXML((String) sourceMap.get("xml")));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("fail parsing xml");
                }
            } else {
                taskList = parseJSONTask(sourceMap);
            }
        }
        for (ITaskBuilder task : taskList) {
            try {
                TriggerKey key = task.getTrigger().getKey();
                if (TriggerState.BLOCKED.equals(this.schedule.getTaskState(key))) {
                    log.debug("task is BLOCKED, add to sync queue." + key);
                    this.task_sync.putTask(task);
                } else {
                    log.debug("add task:" + task.getTrigger().getKey());
                    this.schedule.addTask(task, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                    taskList.add(TaskDirector.getBuilder(dataMap, task_category));
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
    private List<ITaskBuilder> parseJSONTask(Map<String, Object> sourceMap) {
        Map<String, String> planDict = new HashMap<String, String>();
        Map<String, String> taskDict = new HashMap<String, String>();
        Map<String, String> scriptDict = new HashMap<String, String>();
        Map<String, String> taskdataDict = new HashMap<String, String>();
        Map<String, String> taskscriptDict = new HashMap<String, String>();
        Map<String, String> taskdatadetailDict = new HashMap<String, String>();

        planDict.put("ifInstance", "instant");

        taskDict.put("planId", "plan_id");
        planDict.put("planId", "plan_id");

        taskDict.put("taskId", "task_id");
        taskdataDict.put("taskId", "task_id");
        taskscriptDict.put("taskId", "task_id");

        taskdataDict.put("dataId", "data_id");
        taskdatadetailDict.put("dataId", "data_id");

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
        dict.put("src", scriptDict);                    // task : src       1:1
        dict.put("taskData", taskdataDict);             // task : data      1:1
        dict.put("taskScript", taskscriptDict);
        dict.put("taskDataDetail", taskdatadetailDict); // task : detail    1:

        Mapper.transfer(sourceMap, dict);

        System.out.println(sourceMap.get("plan"));

        com.google.gson.Gson pretty;
        pretty = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
        //        System.out.println(pretty.toJson(sourceMap));

        List<ITaskBuilder> taskList = new LinkedList<ITaskBuilder>();

        Map<String, Map<String, Object>> planMap = new HashMap<String, Map<String, Object>>();
        {// TODO plan 这个包并不需要, 可以整合到Task里
            List<Map<String, Object>> planList = Caster.cast(sourceMap.get("plan"));
            for (Map<String, Object> plan : planList) {
                planMap.put((String) plan.get("plan_id"), plan);
            }
        }
        System.out.println("planMap:" + planMap);

        Map<String, Object> scriptMap = new HashMap<String, Object>();
        {
            List<Map<String, Object>> scriptList = Caster.cast(sourceMap.get("src"));
            for (Map<String, Object> script : scriptList) {
                scriptMap.put((String) script.get("script_id"), script);
            }
        }
        System.out.println("scriptMap:" + scriptMap);

        Map<String, String> task2script = new HashMap<String, String>();
        {
            List<Map<String, String>> task2scriptList = Caster.cast(sourceMap.get("taskScript"));
            for (Map<String, String> t2s : task2scriptList) {
                task2script.put(t2s.get("task_id"), t2s.get("script_id"));
            }
        }
        System.out.println("task2script:" + task2script);

        Map<String, Map<String, Object>> dataMap = new HashMap<String, Map<String, Object>>();
        {
            List<Map<String, Object>> dataList = Caster.cast(sourceMap.get("taskData"));
            for (Map<String, Object> data : dataList) {
                dataMap.put((String) data.get("script_id"), data);
            }
        }
        System.out.println("dataMap:" + dataMap);

        Map<String, List<Object>> detailListMap = new HashMap<String, List<Object>>();
        {
            List<Map<String, Object>> detailList = Caster.cast(sourceMap.get("taskDataDetail"));
            for (Map<String, Object> detail : detailList) {
                String dataId = (String) detail.get("data_id");
                if (!detailListMap.containsKey(dataId)) {
                    detailListMap.put(dataId, new LinkedList<Object>());
                }
                List<Object> list = detailListMap.get(dataId);
                list.add(detail);
            }
        }
        System.out.println("detailListMap:" + detailListMap);

        List<Map<String, Object>> tasks = Caster.cast(sourceMap.get("task"));
        for (Map<String, Object> task : tasks) {
            Map<String, Object> taskData = new HashMap<String, Object>();
            {// TODO temply
                task.put("cron", planMap.get(task.get("plan_id")).get("cron"));
                task.put("instant", planMap.get(task.get("plan_id")).get("instant"));
                task.put("task_group", "AITASK");
                task.put("task_category", Integer.toString(TASK_TYPE_BAT));
            }

            String taskId = (String) task.get("task_id");
            String scriptId = task2script.get(taskId);
            taskData.put("task", task);
            taskData.put("script", scriptMap.get(scriptId));

            Map<String, Object> data = dataMap.get(scriptId);
            data.put("detail", detailListMap.get(data.get("data_id")));
            taskData.put("data", data);
            //            System.out.println(pretty.toJson(taskData));
            try {
                String task_category = (String) task.get("task_category");
                taskList.add(TaskDirector.getBuilder(taskData, task_category));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return taskList;
    }
}

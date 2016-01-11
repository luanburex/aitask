package com.ai.app.aitask.schedule;

import java.net.ConnectException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.ContentType;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.Trigger.TriggerState;

import com.ai.app.aitask.common.Configuration;
import com.ai.app.aitask.net.RequestWorker;
import com.ai.app.aitask.task.TaskDirector;
import com.ai.app.aitask.task.builder.ITaskBuilder;

public class TaskFetch {

    private final static Log log              = LogFactory.getLog(TaskFetch.class);
    private TaskSchedule     ts               = null;
    private TaskSyncRunable  task_sync        = null;
    private Thread           task_sync_thread = null;
    private Configuration    config;

    public TaskFetch(TaskSchedule ts, long sync_task_interval_time) {
        this.ts = ts;
        this.config = Configuration.getInstance("client.properties");
        ;
        this.task_sync = new TaskSyncRunable(this.ts);
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
        }
        log.info("resp msg:" + worker.getResponseMessage());
        log.info("resp code:" + worker.getResponseContent());
        String task_xml = worker.getResponseContent();
        if (null == task_xml || task_xml.trim().isEmpty()) {
            log.error("no effective content");
        } else {
            try {
                Document document = DocumentHelper.parseText(task_xml);
                Element root = document.getRootElement();
                for (Object o : root.elements("task")) {
                    Element e = (Element) o;
                    ITaskBuilder tb = TaskDirector.generateTaskBuilderByXml(e.asXML());
                    if (TriggerState.BLOCKED.equals(this.ts.getTaskState(tb.getTrigger().getKey()))) {
                        log.debug("task is BLOCKED, add to sync queue." + tb.getTrigger().getKey());
                        this.task_sync.putTask(tb.getTrigger().getKey(), e.asXML());
                    } else {
                        log.debug("add task:" + tb.getTrigger().getKey());
                        this.ts.addTask(tb, true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

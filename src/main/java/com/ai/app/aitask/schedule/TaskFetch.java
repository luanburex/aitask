package com.ai.app.aitask.schedule;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.Trigger.TriggerState;

import com.ai.app.aitask.common.Configuration;
import com.ai.app.aitask.net.RequestWorker;
import com.ai.app.aitask.task.TaskDirector;
import com.ai.app.aitask.task.builder.ITaskBuilder;

public class TaskFetch {

    private final static Log  log              = LogFactory.getLog(TaskFetch.class);
    private TaskSchedule      ts               = null;
    private TaskSyncRunable   task_sync        = null;
    private Thread            task_sync_thread = null;
    private Configuration config;

    public TaskFetch(TaskSchedule ts, long sync_task_interval_time) {
        this.ts = ts;
        this.config = Configuration.getInstance("client.properties");;
        this.task_sync = new TaskSyncRunable(this.ts);
        this.task_sync.setIntervalTime(sync_task_interval_time);
        this.task_sync_thread = new Thread(this.task_sync);
        this.task_sync_thread.start();
    }

    public void fetch() throws Exception {
        String url = config.getProperty(null, "aitask.sync.url");
        String agent_name = config.getProperty(null, "aitask.name");

        // 取任务

        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("agent_name", agent_name));
        String content = EntityUtils.toString(new UrlEncodedFormEntity(pairs, "UTF-8"));

        RequestWorker worker = new RequestWorker(url);
        worker.Post(content,
                ContentType.APPLICATION_JSON);
        System.out.println(worker.getResponseMessage());
        System.out.println(worker.getResponseContent());

        StringBuffer buffer = new StringBuffer();
        buffer.append("agent_name").append("=").append(agent_name);
        worker.Post(buffer.toString(), ContentType.APPLICATION_JSON);
        String task_xml = worker.getResponseContent();
        // TODO String task_xml = HttpClient.post(url, "agent_name=" + agent_name, "x-www-form-urlencoded");

        Document document = DocumentHelper.parseText(task_xml);
        Element root = document.getRootElement();

        Iterator<Element> elements = root.elementIterator("task");
        while (elements.hasNext()) {
            Element current_element = elements.next();
            ITaskBuilder tb = TaskDirector.generateTaskBuilderByXml(current_element.asXML());
            if (TriggerState.BLOCKED.equals(this.ts.getTaskState(tb.getTrigger().getKey()))) {
                log.debug("task is BLOCKED, add to sync queue." + tb.getTrigger().getKey());
                this.task_sync.putTask(tb.getTrigger().getKey(), current_element.asXML());
            } else {
                log.debug("add task:" + tb.getTrigger().getKey());
                this.ts.addTask(tb, true);
            }
        }
    }

}

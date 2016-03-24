package com.ai.app.aitask.schedule;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.net.RequestWorker;
import com.ai.app.aitask.task.TaskBuilderFactory;
import com.ai.app.aitask.task.builder.ITaskBuilder;

public class TaskFetcher implements Constants {
    protected final static Logger log = Logger.getLogger(TaskFetcher.class);
    private Config                config;

    public TaskFetcher() {
        this.config = Config.instance("client.properties");
    }

    private String getIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "127.0.0.1";
        }
    }

    public List<ITaskBuilder> fetch(String taskId, String remote) {
        String url = config.getProperty(null, "aitask.sync.url");

        HashMap<String, String> queryPairs = new HashMap<String, String>();
        queryPairs.put("agentName", config.getProperty(null, "aitask.name"));
        queryPairs.put("ip", getIp());
        if (null != taskId) {
            queryPairs.put("taskId", taskId);
        }
        if (null != remote) {
            url = url.replaceFirst("://.*:", "://".concat(remote).concat(":"));
        }

        RequestWorker worker = new RequestWorker(url, null);
        try {
            worker.post(RequestWorker.formEntity(queryPairs), ContentType.APPLICATION_JSON);
        } catch (ConnectException e) {
            log.error("req : failed connecting");
            return new ArrayList<ITaskBuilder>();
        } finally {
            log.info("resp msg:" + worker.getRespMsg());
            log.info("resp txt:" + worker.getRespContent());
        }

        String content = worker.getRespContent();
        if (null == content || content.trim().isEmpty()) {
            log.error("no effective content");
            return new ArrayList<ITaskBuilder>();
        } else {
            return TaskBuilderFactory.parseBuilder(content);
        }
    }
}

package com.ai.app.aitask.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.Trigger.TriggerState;

import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.schedule.TaskFetch;
import com.ai.app.aitask.schedule.TaskSchedule;
import com.ai.app.aitask.task.TaskDirector;
import com.ai.app.aitask.task.tasks.ITaskBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class TaskArrangeServlet extends HttpServlet implements Constants {
    private static final long serialVersionUID = 1L;
    private final static Log  log              = LogFactory.getLog(TaskArrangeServlet.class);
    private TaskSchedule      ts;

    public TaskArrangeServlet() {
        ScheduleDaemon sd = ScheduleDaemon.instance();
        this.ts = sd.getTaskSchedule();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("query:" + req.getQueryString());
        String taskId = req.getParameter("taskId");
        String sceneId = req.getParameter("sceneId");

        StringBuffer buffer = new StringBuffer();
        {
            if (req.getContentLength() > -1) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        req.getInputStream(), "UTF-8"));
                // GBK for some special...
                for (String temp = reader.readLine(); temp != null;) {
                    buffer.append(temp).append(LINE_SEPARATOR);
                    temp = reader.readLine();
                }
            }
            System.out.println(buffer.toString());
        }
        System.out.println("来自:"+req.getRemoteAddr());

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        Writer writer = resp.getWriter();
        writer.write("收到:" + buffer.toString());
        writer.flush();

        {
        String url_path = "";
        // RequestWorker worker = new RequestWorker(url_path);
        // worker.setQueryParameter("taskId", taskId);
        // JsonElement dataset = new JsonParser().parse(worker.Get().getResponseContent());
        }
        
        if(buffer.length() > 0){
            Document document;
            try {
                document = DocumentHelper.parseText(buffer.toString());
                Element root = document.getRootElement();
                for (Object o : root.elements("task")) {
                    Element e = (Element) o;
                    ITaskBuilder tb = TaskDirector.generateTaskBuilderByXml(e.asXML());
                    log.debug("add task:" + tb.getTrigger().getKey());
                    ts.addTask(tb, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("nothin");
        }

        /** Fetch Task */
        // request = new RequestWorker(config.getProperty(Constants.URL_FETCH_TASK));
        // request.setQueryParameter("taskId", taskId);
        // request.setQueryParameter("sceneId", sceneId);
        //
        // String taskJson = request.Get().getResponseContent();
        // System.out.println("TJ:"+taskJson);
        // {
        // /**
        // * rename fields by Alex
        // */
        // JSONObject taskObject = JsonUtil.getJava(taskJson, JSONObject.class);
        //
        // taskObject.put("id", taskId);
        // taskObject.put("interval", taskObject.remove("runIntervalTime"));
        // taskObject.put("frequency", taskObject.remove("runTimes"));
        // taskJson = taskObject.toString();
        // }
        // Task task = JsonUtil.getJava(taskJson, Task.class);
        // /** Fetch Scene */
        // request = new RequestWorker(config.getProperty(Constants.URL_FETCH_SCENE));
        // request.setQueryParameter("sceneId", sceneId);
        // Scene scene = JsonUtil.getJava(request.Get().getResponseContent(), Scene.class);
        //
        // HashMap<String, Dataset> datasetMap = new HashMap<String, Dataset>();
        // HashMap<String, Testcase> testcaseMap = new HashMap<String, Testcase>();
        // for (Plan plan : task.getPlans()) {
        // if (!testcaseMap.containsKey(plan.getTestcaseid())) {
        // /** Fetch Testcase */
        // request = new RequestWorker(config.getProperty(Constants.URL_FETCH_TESTCASE));
        // request.setQueryParameter("taskId", taskId);
        // request.setQueryParameter("sceneId", sceneId);
        // request.setQueryParameter("testcaseId", plan.getTestcaseid());
        // String testcaseJson = request.Get().getResponseContent();
        // {
        // /**
        // * add id field
        // *
        // * by Alex
        // */
        // JSONObject testcaseObject = JsonUtil.getJava(testcaseJson, JSONObject.class);
        // testcaseObject.put("id", testcaseObject.get("name"));
        // testcaseJson = testcaseObject.toString();
        // }
        // Testcase testcase = JsonUtil.getJava(testcaseJson, Testcase.class);
        // testcaseMap.put(plan.getTestcaseid(), testcase);
        // }
        //
        // if (!datasetMap.containsKey(plan.getDatasetid())) {
        // /** Fetch Dataset */
        // request = new RequestWorker(config.getProperty(Constants.URL_FETCH_DATASET));
        // request.setQueryParameter("taskId", taskId);
        // request.setQueryParameter("sceneId", sceneId);
        // request.setQueryParameter("datasetId", plan.getDatasetid());
        // request.setQueryParameter("testcaseId", plan.getTestcaseid());
        //
        // String datasetJson = request.Get().getResponseContent();
        // {
        // /**
        // * add id field.
        // *
        // * add default 'expect', 'value' field for argument.
        // *
        // * by Alex
        // */
        // JSONObject datasetObject = JsonUtil.getJava(datasetJson, JSONObject.class);
        // datasetObject.put("id", datasetObject.get("name"));
        // JSONObject stepsObject = datasetObject.getJSONObject("arguments");
        // for(Object stepKey : stepsObject.keySet()) {
        // JSONObject argumentsObject = stepsObject.getJSONObject((String)stepKey);
        // for(Object argsKey : argumentsObject.keySet()) {
        // JSONObject argumentObject = argumentsObject.getJSONObject((String)argsKey);
        // System.out.println("arg:"+argsKey);
        // if (null == argumentObject.get("expect")) {
        // argumentObject.put("expect", JSONNull.getInstance());
        // }
        // if (null == argumentObject.get("value")) {
        // argumentObject.put("value", JSONNull.getInstance());
        // }
        // }
        // }
        // datasetJson = datasetObject.toString();
        // }
        // Dataset dataset = JsonUtil.getJava(datasetJson, Dataset.class);
        // datasetMap.put(plan.getDatasetid(), dataset);
        // /**
        // * TODO Use better json library plz.
        // *
        // * Fck Json-Lib.
        // *
        // * by Alex 20141210
        // */
        // for (String key : dataset.getArguments().keySet()) {
        // MorphDynaBean argumentsBean = (MorphDynaBean) (Object) dataset.getArguments().get(key);
        // Map<String, Argument> argumentsMap = new HashMap<String, Argument>();
        // for (DynaProperty argumentsProperty : argumentsBean.getDynaClass().getDynaProperties()) {
        // MorphDynaBean argumentBean = (MorphDynaBean) argumentsBean.get(argumentsProperty.getName());
        // Argument argument = new Argument();
        // argument.setExpect((String) argumentBean.get("expect"));
        // argument.setValue((String) argumentBean.get("value"));
        // argumentsMap.put(argumentsProperty.getName(), argument);
        // }
        // dataset.getArguments().put(key, argumentsMap);
        // }
        // }
        // }
        //
        // task.scene = scene;
        // task.datasetMap = datasetMap;
        // task.testcastMap = testcaseMap;
        // listener.onNetEvent(NetEvent.TaskArrange, task);
    }
}

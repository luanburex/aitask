package com.ai.app.aitask.net.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.google.gson.Gson;

public class StepQueryServlet extends HttpServlet {
    private static final long serialVersionUID = 880300319427022262L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
    IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String taskId = req.getParameter("taskId");
        String dataId = req.getParameter("dataId");
        //TODO fix error
        //        for (JobExecutionContext context : ScheduleDaemon.instance().getTaskSchedule().getTasks()) {
        //            JobDataMap jobdatamap = context.getMergedJobDataMap();
        //            Map<String, Map<String, Object>> datamap = Caster.cast(jobdatamap.get("datamap"));
        //            if (!datamap.get("task").get("task_id").equals(taskId)) {
        //                continue;
        //            } else if (!datamap.get("data").get("data_id").equals(dataId)) {
        //                continue;
        //            } else {
        //                // TODO 暂时还没有实质内容，可能得脚本下来之后才知道了
        //                Map<String, Object> stepMap = new HashMap<String, Object>();
        //                stepMap.put("startTime", System.currentTimeMillis());
        //                stepMap.put("stepId", "步骤Id");
        //                stepMap.put("stepName", "步骤名称");
        //                stepMap.put("stepDesc", "步骤描述");
        //                stepMap.put("stepExpectResult", "期望结果");
        //                stepMap.put("result", "执行结果");
        //                stepMap.put("endTime", System.currentTimeMillis());
        //
        //                Map<String, Object> map = new HashMap<String, Object>();
        //                map.put("stepLog", new Object[]{stepMap});
        //                Writer writer = resp.getWriter();
        //                writer.write(new Gson().toJson(map));
        //                writer.flush();
        //                return;
        //            }
        //        }
    }
}

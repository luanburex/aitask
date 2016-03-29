package com.ai.app.aitask.net.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Alex Xu
 */
public class DataQueryServlet extends HttpServlet {
    private static final long serialVersionUID = -2481988744048456554L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
    IOException {
        doPost(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String taskId = req.getParameter("taskId");
        //TODO fix error
        //        for (JobExecutionContext context : ScheduleDaemon.instance().getTaskSchedule().getTasks()) {
        //            JobDataMap jobdatamap = context.getMergedJobDataMap();
        //            Map<String, Map<String, Object>> datamap = Caster.cast(jobdatamap.get("datamap"));
        //            if (datamap.get("task").get("task_id").equals(taskId)) {
        //                Long start = (Long) jobdatamap.get("start_time");
        //                Long end = (Long) jobdatamap.get("end_time");
        //                if (null != start && null == end) {
        //                    String dataId = (String) datamap.get("data").get("data_id");
        //                    String dataName = (String) datamap.get("data").get("data_name");
        //
        //                    resp.setCharacterEncoding("UTF-8");
        //                    resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        //
        //                    Map<String, String> map = new HashMap<String, String>();
        //                    map.put("dataId", dataId);
        //                    map.put("dataName", dataName);
        //                    Writer writer = resp.getWriter();
        //                    writer.write(new Gson().toJson(map));
        //                    writer.flush();
        //                    return;
        //                }
        //            }
        //        }
    }
}

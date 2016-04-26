package com.ai.app.aitask.net.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.deamon.TaskSyncDaemon;
import com.ai.app.aitask.schedule.TaskFetcher;

/**
 * @author Alex Xu
 */
public class TaskArrangeServlet extends HttpServlet implements Constants {
    private static final long     serialVersionUID = 4588897907403234206L;
    protected static final Logger logger              = Logger.getLogger(TaskArrangeServlet.class);
    private TaskFetcher           fetcher          = new TaskFetcher();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        doPost(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String taskid = req.getParameter("taskId");

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        Writer writer = resp.getWriter();
        writer.write("task arrange received:" + taskid);
        writer.flush();
        writer.close();

        TaskSyncDaemon.instance().syncTask(fetcher.fetch(taskid, getIpAddr(req)));
    }

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (null == ip || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (null == ip || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (null == ip || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

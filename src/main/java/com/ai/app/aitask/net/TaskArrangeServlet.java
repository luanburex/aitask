package com.ai.app.aitask.net;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.schedule.TaskFetcher;

public class TaskArrangeServlet extends HttpServlet implements Constants {
    private static final long  serialVersionUID = 4588897907403234206L;
    protected final static Log log              = LogFactory.getLog(TaskArrangeServlet.class);
    private TaskFetcher        fetcher;
    {
        fetcher = new TaskFetcher(ScheduleDaemon.instance().getTaskSchedule());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
    IOException {
        doPost(req, resp);
    }

    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
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

        fetcher.fetch(taskid, getIpAddr(req));
    }
}

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
    private static final long  serialVersionUID = 1L;
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("from : " + req.getRemoteAddr());
        System.out.println("query : " + req.getQueryString());

        String taskid = req.getParameter("taskId");

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        Writer writer = resp.getWriter();
        writer.write("收到:" + taskid);
        writer.flush();

        fetcher.fetch(taskid);
    }
}

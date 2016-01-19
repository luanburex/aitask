package com.ai.app.aitask.task.builder.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;

import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.common.HttpClient;
import com.ai.app.aitask.task.builder.ITask;
import com.ai.app.aitask.task.excutor.IDataPreparer;
import com.ai.app.aitask.task.excutor.IExecutor;
import com.ai.app.aitask.task.excutor.IResultFetcher;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class SerialTask implements ITask, Constants {

    transient final static private Logger log              = Logger.getLogger(SerialTask.class);

    private volatile boolean              isJobInterrupted = false;
    private volatile Thread               thisThread;
    private volatile IExecutor            executor;

    private long                          start;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        thisThread = Thread.currentThread();
        try {
            context.getMergedJobDataMap().put("start_time", start = System.currentTimeMillis());

            executor = (IExecutor) context.getMergedJobDataMap().get("executor");
            IDataPreparer preparer = (IDataPreparer) context.getMergedJobDataMap().get("preparer");
            IResultFetcher fetcher = (IResultFetcher) context.getMergedJobDataMap().get("result");
            log.info("[" + context.getTrigger().getKey() + "]" + "task execute....");
            try {
                Thread.sleep(1000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (executor == null)
                throw new JobExecutionException("The task's executor is null.");
            if (preparer != null && !isJobInterrupted)
                preparer.prepare(context);

            if (!isJobInterrupted) {
                context.getMergedJobDataMap().put("start_time", System.currentTimeMillis());
                /**
                 * 执行任务
                 */
                int executor_res = executor.run(context);
                context.getMergedJobDataMap().put("end_time", System.currentTimeMillis());
                log.info("[" + context.getTrigger().getKey() + "]" + "task execute return: "
                        + executor_res);
            }
            String result_str = null;
            System.err.println("done?");
            if (fetcher != null && !isJobInterrupted) {
                result_str = fetcher.fetch(context);
                log.info("[" + context.getTrigger().getKey() + "]" + "task fetch result: "
                        + result_str);
                // try {
                // String result = HttpClient.post("http://10.1.55.14:8002/aiga/service?action=receivePost&isconvert=true", result_str,
                // ContentType.TEXT_HTML.getMimeType());
                // System.err.println("result:"+result);
                // } catch (IOException e) {
                // e.printStackTrace();
                // }
            }
        } catch (JobExecutionException je) {
            System.err.println("fail?");
//            je.printStackTrace();
            IResultFetcher fetcher = (IResultFetcher) context.getMergedJobDataMap().get("result");
            if (fetcher != null && !isJobInterrupted) {
                String result_str = fetcher.error(context, je);
                log.info("[" + context.getTrigger().getKey() + "]" + "task fetch result: "
                        + result_str);
                try {
                    BasicNameValuePair p = new BasicNameValuePair("xml", result_str);
                    ArrayList<NameValuePair> l = new ArrayList<NameValuePair>();
                    l.add(p);
                    UrlEncodedFormEntity e = new UrlEncodedFormEntity(l, "UTF-8");
                     String result = HttpClient.post("http://10.1.55.14:8002/aiga/service?action=receivePost&isconvert=true", e,
                     ContentType.APPLICATION_JSON.getMimeType());
                     System.err.println("result:"+result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally { // TODO 异常处理时无法操作context ?
            StringBuffer buf = new StringBuffer();
            buf.append("sta:" + new Date(start));
            buf.append(LINE_SEPARATOR);
            buf.append("end:" + new Date(System.currentTimeMillis()));
            System.err.println(buf);
        }
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        log.info("Serial Task interrup");
        isJobInterrupted = true;
        if (executor != null) {
            log.info("interrupt executor: " + executor.getClass().toString());
            executor.interupt();
        }
        if (thisThread != null) {
            log.info("interrupt this thread: " + thisThread.getName());
            thisThread.interrupt();
        }
    }

    @Override
    public void before(JobExecutionContext context) {

    }

    @Override
    public void after(JobExecutionContext context, JobExecutionException error) {
    }
}
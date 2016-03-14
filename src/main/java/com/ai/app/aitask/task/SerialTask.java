package com.ai.app.aitask.task;

import java.net.ConnectException;
import java.util.Date;

import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.net.RequestWorker;
import com.ai.app.aitask.task.excutor.IDataPreparer;
import com.ai.app.aitask.task.excutor.IExecutor;
import com.ai.app.aitask.task.result.IResultFetcher;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class SerialTask implements ITask, Constants {

    transient final static protected Logger log              = Logger.getLogger(SerialTask.class);

    private volatile boolean                isJobInterrupted = false;
    private volatile Thread                 thisThread;
    private volatile IExecutor              executor;
    private Config                          config;
    private long                            start;
    {
        config = Config.instance("client.properties");
    }
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
            if (executor == null) {
                throw new JobExecutionException("The task's executor is null.");
            }
            if (preparer != null && !isJobInterrupted) {
                preparer.prepare(context);
            }

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
                String url = config.getProperty(null, "aitask.result.url");
                RequestWorker worker = new RequestWorker(url, null);
                try {
                    worker.Post(result_str, ContentType.APPLICATION_JSON);
                } catch (ConnectException e) {
                    // e.printStackTrace();
                    log.error("fail reporting");
                } finally {
                    log.info("resp cod : " + worker.getResponseCode());
                    log.info("resp msg : " + worker.getResponseMessage());
                }
            }
        } catch (JobExecutionException je) {
            System.err.println("fail?");
            context.getMergedJobDataMap().put("end_time", System.currentTimeMillis());
            je.printStackTrace();
            IResultFetcher fetcher = (IResultFetcher) context.getMergedJobDataMap().get("result");
            if (fetcher != null && !isJobInterrupted) {
                String result_str = fetcher.error(context, je);
                log.info("[" + context.getTrigger().getKey() + "]" + "task fetch result: "
                        + result_str);
                String url = config.getProperty(null, "aitask.result.url");
                RequestWorker worker = new RequestWorker(url, null);
                try {
                    worker.Post(result_str, ContentType.APPLICATION_JSON);
                } catch (ConnectException e) {
                    // e.printStackTrace();
                    log.error("fail reporting");
                } finally {
                    log.info("resp cod : " + worker.getResponseCode());
                    log.info("resp msg : " + worker.getResponseMessage());
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
            executor.destroy();
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
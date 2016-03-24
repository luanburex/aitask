package com.ai.app.aitask.task;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.net.RequestWorker;
import com.ai.app.aitask.task.excutor.IExecutor;
import com.ai.app.aitask.task.preparer.IDataPreparer;
import com.ai.app.aitask.task.result.IResultFetcher;

public class AbstractTask implements ITask, Constants {
    protected final static Logger log = Logger.getLogger(AbstractTask.class);

    private volatile boolean      interrupted;
    private volatile Thread       thread;
    private Config                config;
    private String                prefix;
    private Map<String, Object>   runtimeData;
    {
        config = Config.instance("client.properties");
        interrupted = false;
        runtimeData = new HashMap<String, Object>();
    }

    @Override
    public void before(Map<String, Object> trigger, Map<String, Object> datamap) {
        prefix = (String) trigger.get("info");
        IDataPreparer preparer = (IDataPreparer) datamap.get("preparer");
        if (null != preparer && !interrupted) {
            log.info(String.format("[%s] prepare %s", prefix, preparer.getClass()));
            preparer.prepare(datamap);
        }
    }

    @Override
    public void execute(Map<String, Object> trigger, Map<String, Object> datamap) {
        // TODO What will happen when interrupted?

        runtimeData.put("thread", Thread.currentThread());
        runtimeData.put("timeStart", System.currentTimeMillis());

        try {
            IExecutor executor = (IExecutor) datamap.get("executor");
            runtimeData.put("executor", executor);
            log.info(String.format("[%s] to execute", prefix));
            if (null == executor) {
                // TODO Executor is Null!!! tell somebody...
            }
            int result = executor.execute(datamap);
            log.info(String.format("[%s] executed, return(%d)", prefix, result));
            runtimeData.put("exitCode", result);
        } catch (Exception e) {
            e.printStackTrace();
            runtimeData.put("exception", e);
        } finally {
            runtimeData.put("timeFinish", System.currentTimeMillis());
        }
    }

    @Override
    public void interrupt() {
        interrupted = true;
        log.info(String.format("[%s] Task interrupt", prefix));
        IExecutor executor = (IExecutor) runtimeData.get("executor");
        if (executor != null) {
            log.info(String.format("[%s] interrupt executor : %s", prefix, executor.getClass()));
            executor.interrupt();
        }
        if (thread != null) {
            log.info(String.format("[%s] interrupt thread : %s", prefix, thread.getName()));
            thread.interrupt();
        }
    }
    @Override
    public void after(Map<String, Object> trigger, Map<String, Object> datamap) {
        IResultFetcher fetcher = (IResultFetcher) datamap.get("fetcher");
        if (runtimeData.containsKey("exception")) { // fail
            if (null != fetcher) {
                String result = fetcher.error(datamap, (Exception) runtimeData.get("exception"));
                log.info(String.format("[%s] execut failed : %d", prefix, result));
                //TODO RequestWorker should move to a TaskFinishListener
                String url = config.getProperty(null, "aitask.result.url");
                RequestWorker worker = new RequestWorker(url, null);
                try {
                    worker.post(result, ContentType.APPLICATION_JSON);
                } catch (ConnectException e) {
                    log.error(String.format("[%s] report failed :  %s", prefix, e.getMessage()));
                } finally {
                    log.info(String.format("[%s] resp cod : %s", prefix, worker.getRespCode()));
                    log.info(String.format("[%s] resp msg : %s", prefix, worker.getRespMsg()));
                }
            }
        } else {
            if (null != fetcher) {
                String result = fetcher.fetch(datamap);
                log.info(String.format("[%s] execut finished : %d", prefix, result));
                String url = config.getProperty(null, "aitask.result.url");
                RequestWorker worker = new RequestWorker(url, null);
                try {
                    worker.post(result, ContentType.APPLICATION_JSON);
                } catch (ConnectException e) {
                    log.error(String.format("[%s] report failed : %s", prefix, e.getMessage()));
                } finally {
                    log.info(String.format("[%s] resp cod : %s", prefix, worker.getRespCode()));
                    log.info(String.format("[%s] resp msg : %s", prefix, worker.getRespMsg()));
                }
            }
        }
    }
}
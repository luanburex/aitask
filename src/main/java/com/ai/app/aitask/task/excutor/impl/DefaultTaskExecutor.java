package com.ai.app.aitask.task.excutor.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ai.app.aitask.common.ProcessWorker;
import com.ai.app.aitask.task.excutor.IExecutor;

/**
 * Use System shell to run cmd command.
 * 
 * @author renzq
 * @author Alex Xu
 */
public class DefaultTaskExecutor implements IExecutor {

    protected final static Logger log      = Logger.getLogger(DefaultTaskExecutor.class);
    private ProcessWorker         process  = null;
    private String[]              commands = null;

    public String getOutput() {
        return process.getStandOutput();
    }

    public String getError() {
        return process.getErrorOutput();
    }

    public DefaultTaskExecutor(String... commands) {
        this.commands = commands;
    }

    public void killTask(String process_name) throws IOException {
        ProcessWorker process = new ProcessWorker(DEFAULT_CHARSET);
        process.process("taskkill", "/T", "/F", "/IM", process_name);

        try {
            Thread.sleep(2000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (taskExist(process_name)) {
            log.error("process kill error:" + process_name);
        }
    }

    private boolean taskExist(final String process_name) throws IOException {
        ProcessWorker process = new ProcessWorker(DEFAULT_CHARSET) {
            @Override
            protected void handleStandOutput(String content) {
                if (content.contains(process_name)) {
                    destroy();  // force process close will return '1' as result.
                }
                super.handleStandOutput(content);
            }
        };
        return 1 == process.process("tasklist");
    }
    @Override
    public int execute(Map<String, Object> datamap) {
        log.info("executing:" + Arrays.toString(commands));
        //TODO run run run
        process = new ProcessWorker(DEFAULT_CHARSET);
        log.info("process begin");
        int result = process.process(commands);
        log.info("process end");
        log.info("process stdout: " + process.getStandOutput());
        if (!process.getErrorOutput().isEmpty()) {
            log.error("process erroput: " + process.getErrorOutput());
        }
        return result;
    }
    @Override
    public void interrupt() {
        log.info("process destroyed");
        if (this.process != null) {
            this.process.destroy();
        }
    }
}

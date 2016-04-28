package com.ai.app.aitask.task.executor.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.ProcessWorker;
import com.ai.app.aitask.task.executor.IExecutor;

/**
 * Use System shell to run cmd command.
 * 
 * @author renzq
 * @author Alex Xu
 */
public class ProcessTaskExecutor implements IExecutor {

    protected static final Logger logger  = Logger.getLogger(ProcessTaskExecutor.class);
    private Map<String, String>   properties;
    private ProcessWorker         process = null;

    public String getOutput() {
        return process.getStandOutput();
    }

    public String getError() {
        return process.getErrorOutput();
    }

    public ProcessTaskExecutor(Map<String, String> properties) {
        this.properties = properties;
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
            logger.error("process kill failed:" + process_name);
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
        return (1 == process.process("tasklist"));
    }
    @Override
    public int execute(Map<String, Object> datamap) {
        String command = properties.get("command");
        //TODO make it better
        Map<String, Object> basedatamap = Caster.cast(datamap.get("datamap"));
        Map<String, String> scriptdata = Caster.cast(basedatamap.get("script"));
        //        command = command.replaceAll("%exedata%", pathExedata.replaceAll("\\\\", "\\\\\\\\"));
        command = command.replaceAll("%scriptname%", scriptdata.get("scriptName"));
        String[] commands = command.split(" ");

        logger.info("executing:" + Arrays.toString(commands));
        //TODO run run run
        process = new ProcessWorker(DEFAULT_CHARSET);
        logger.info("process begin");
        int result = process.process(commands);
        logger.info("process end");
        if (!process.getStandOutput().isEmpty()) {
            logger.info("process stdout: " + process.getStandOutput());
        }
        if (!process.getErrorOutput().isEmpty()) {
            logger.error("process erroput: " + process.getErrorOutput());
        }
        return result;
    }
    @Override
    public void interrupt() {
        logger.info("process destroyed");
        if (this.process != null) {
            this.process.destroy();
        }
    }
}

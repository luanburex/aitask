package com.ai.app.aitask.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class ProcessWorker implements Constants {
    protected Logger     log = Logger.getLogger(ProcessWorker.class);
    private StringBuffer standBuffer;
    private StringBuffer errorBuffer;
    private Charset      charset;
    private Process      process;

    public ProcessWorker() {
        this("UTF-8");
    }

    public ProcessWorker(String charset) {
        this.charset = Charset.forName(charset);
        this.standBuffer = new StringBuffer();
        this.errorBuffer = new StringBuffer();
    }

    public final String getStandOutput() {
        return standBuffer.toString();
    }

    public final String getErrorOutput() {
        return errorBuffer.toString();
    }

    protected void handleStandOutput(String content) {
        log.debug("process stdout :" + content);
    }

    protected void handleErrorOutput(String content) {
        log.debug("process errout :" + content);
    }

    public int process(String... commands) {
        try {
            if (commands.length == 1) {
                process = Runtime.getRuntime().exec(commands[0]);
            } else {
                ProcessBuilder builder = new ProcessBuilder(commands);
                process = builder.start();
            }
            log.info("process cmd : " + Arrays.toString(commands));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            standBuffer.setLength(0);
            errorBuffer.setLength(0);

            Reader standStreamReader = new InputStreamReader(process.getInputStream(), charset);
            Reader errorStreamReader = new InputStreamReader(process.getErrorStream(), charset);
            BufferedReader stand = new BufferedReader(standStreamReader);
            BufferedReader error = new BufferedReader(errorStreamReader);
            try {
                for (String line = stand.readLine(); null != line; line = stand.readLine()) {
                    standBuffer.append(line).append(LINE_SEPARATOR);
                    handleStandOutput(line);
                }
                for (String line = error.readLine(); null != line; line = stand.readLine()) {
                    errorBuffer.append(line).append(LINE_SEPARATOR);
                    handleErrorOutput(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    stand.close();
                    error.close();
                    standStreamReader.close();
                    errorStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int result = process.waitFor();
            log.info("process result : " + result);
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void destroy() {
        if (null != process) {
            process.destroy();
        }
    }
}

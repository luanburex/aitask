package com.ai.app.aitask.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * @author Alex Xu
 */
public class ProcessWorker implements Constants {
    protected Logger     logger = Logger.getLogger(ProcessWorker.class);
    private StringBuffer standBuffer;
    private StringBuffer errorBuffer;
    private Charset      charset;
    private Process      process;
    private File         dir;

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
        logger.debug("process stdout :" + content);
    }

    protected void handleErrorOutput(String content) {
        logger.debug("process errout :" + content);
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public int process(String... commands) {
        try {
            if (commands.length == 1) {
                process = Runtime.getRuntime().exec(commands[0], null, dir);
            } else {
                ProcessBuilder builder = new ProcessBuilder(commands);
                builder.directory(dir);
                process = builder.start();
            }
            logger.info("process cmd : " + Arrays.toString(commands));
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
                for (String line = error.readLine(); null != line; line = error.readLine()) {
                    errorBuffer.append(line).append(LINE_SEPARATOR);
                    handleErrorOutput(line);
                }
                for (String line = stand.readLine(); null != line; line = stand.readLine()) {
                    standBuffer.append(line).append(LINE_SEPARATOR);
                    handleStandOutput(line);
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
            logger.info("process result : " + result);
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

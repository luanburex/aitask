package com.ai.app.aitask.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class Processor implements Constants {
    protected Logger     log = Logger.getLogger(Processor.class);
    private StringBuffer standBuffer;
    private StringBuffer errorBuffer;
    private List<String> commandList;
    private boolean      processing;
    private Charset      charset;
    private Process      process;
    private InputStream  i;

    public Processor() {
        this("UTF-8");
        //        this("GBK");
    }

    public Processor(String charset) {
        this.commandList = new ArrayList<String>();
        this.processing = false;
        this.charset = Charset.forName(charset);
    }

    public Processor attachCommands(String... commands) {
        this.commandList.addAll(Arrays.asList(commands));
        return this;
    }

    public void handleStandardOutput(String content) {
        log.debug("STDINPUT :" + content);
    }

    public void handleErrorOutput(String content) {
        log.debug("ERRINPUT :" + content);
    }

    public String process() {
        String[] commands = new String[commandList.size()];
        commandList.toArray(commands);
        System.out.println("cmds : " + Arrays.toString(commands));
        try {
            //TODO ProcessBuilder
            process = Runtime.getRuntime().exec(commands);
            standBuffer = new StringBuffer();
            errorBuffer = new StringBuffer();

            processing = true;
            Reader standReader = new InputStreamReader(process.getInputStream(), charset);
            Reader errorReader = new InputStreamReader(process.getErrorStream(), charset);
            BufferedReader standBufferReader = new BufferedReader(standReader);
            BufferedReader errorBufferReader = new BufferedReader(errorReader);
            //TODO 问题很大
            //            while (processing) {
            try {
                String line;
                System.out.println();
                System.out.println();
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                System.out.println("r:"+standBufferReader.ready());
                System.out.println("l:"+standBufferReader.readLine());
                while (standBufferReader.ready()) {
                    line = standBufferReader.readLine();
                    standBuffer.append(line).append(LINE_SEPARATOR);
                    handleStandardOutput(line);
                }
                while (errorBufferReader.ready()) {
                    line = errorBufferReader.readLine();
                    errorBuffer.append(line).append(LINE_SEPARATOR);
                    handleErrorOutput(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //                try {
                //                    standBufferReader.close();
                //                    errorBufferReader.close();
                //                    standReader.close();
                //                    errorReader.close();
                //                } catch (IOException e) {
                //                    e.printStackTrace();
                //                }
            }
            //            }
            //            }).start();
            int result = process.waitFor();
            System.out.println("process result : " + result);
            //            processing = false;
            return Integer.toString(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void destroy() {
        if (null != process) {
            process.destroy();
        }
    }
}

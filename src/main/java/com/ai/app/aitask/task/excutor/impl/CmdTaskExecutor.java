package com.ai.app.aitask.task.excutor.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ai.app.aitask.task.excutor.IExecutor;

/**
 * Use System shell to run cmd command.
 * 
 * @author Administrator
 *
 */
public class CmdTaskExecutor implements IExecutor {

    protected transient final static Logger log             = Logger.getLogger(CmdTaskExecutor.class);
    public static String                    default_charset = "GBK";

    private Process                         exectionProcess = null;
    private String                          cmd             = null;
    private String                          execute_out     = null;
    private String                          execute_error   = null;

    public String getExecute_out() {
        return execute_out;
    }

    public String getExecute_error() {
        return execute_error;
    }

    public CmdTaskExecutor(String cmd) {
        this.cmd = cmd;
    }

    public void taskkill(String process_name) throws IOException {

        Process process_kill_task = Runtime.getRuntime().exec("taskkill /T /F /IM " + process_name);
        Scanner in = new Scanner(process_kill_task.getInputStream());
        while (in.hasNextLine()) {
            // log.debug(in.nextLine());
        }

        try {
            Thread.sleep(2000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.existsTask(process_name)) {
            log.error("process kill error:" + process_name);
        }
        in.close();
    }
    private boolean existsTask(String process_name) throws IOException {
        Process process = Runtime.getRuntime().exec("tasklist");

        Scanner in = new Scanner(process.getInputStream());
        while (in.hasNextLine()) {
            String p = in.nextLine();
            if (p.contains(process_name)) {
                in.close();
                return true;
            }
        }
        in.close();
        return false;
    }

    /**
     * run cmd command.
     * 
     * @param cmd
     * @return
     * @throws Exception
     */
    @Override
    public int run(JobExecutionContext context) throws JobExecutionException {
        if (context != null)
            log.info("[" + context.getTrigger().getKey() + "]" + "start run cmd: " + cmd);

        try {
            this.exectionProcess = Runtime.getRuntime().exec(cmd);
            // BufferedInputStream in = new BufferedInputStream(this.exectionProcess.getInputStream());
            // byte[] bytes = new byte[4096];
            // while (in.read(bytes) != -1) {execute_out += new String(bytes, "UTF-8");}
            this.execute_out = "";
            this.execute_error = "";
            InputStream in = this.exectionProcess.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, default_charset));
            String str = "";
            while ((str = br.readLine()) != null) {
                this.execute_out += str;
            }

            BufferedReader br_err = new BufferedReader(new InputStreamReader(
                    this.exectionProcess.getErrorStream(), default_charset));
            while ((str = br_err.readLine()) != null) {
                this.execute_error += "\n" + str;
            }

            log.info("CMD stdout: " + this.execute_out);
            if (!"".equals(execute_error))
                log.error("CMD error: " + this.execute_error);

            if (context != null)
                log.info("[" + context.getTrigger().getKey() + "]" + "start wait cmd end..: " + cmd);
            int result = this.exectionProcess.waitFor();
            if (context != null)
                log.info("[" + context.getTrigger().getKey() + "]" + "end cmd execution: " + cmd);

            return result;
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }

    }

    /**
     * use process.destroy end the process running.
     */
    @Override
    public void interupt() {
        log.info("destroy process: " + cmd + "\t ,and process object :" + this.exectionProcess);
        if (this.exectionProcess != null) {
            this.exectionProcess.destroy();
            // this.exectionProcess.exitValue();
        }
    }
}

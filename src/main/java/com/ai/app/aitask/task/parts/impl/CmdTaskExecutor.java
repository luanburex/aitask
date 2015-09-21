package com.ai.app.aitask.task.parts.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ai.app.aitask.task.parts.interfaces.IExecutor;

/**
 * Use System shell to run cmd command.
 * @author Administrator
 *
 */
public class CmdTaskExecutor implements IExecutor{

	transient final static private Logger log = Logger.getLogger(CmdTaskExecutor.class);
	private Process exectionProcess = null;
	private String cmd = null;
	
	public CmdTaskExecutor(String cmd){
		this.cmd = cmd;
	}
	
	
	public void taskkill(String process_name) throws IOException {
		
		Process process_kill_task = Runtime.getRuntime().exec("taskkill /T /F /IM " + process_name);
		Scanner in = new Scanner(process_kill_task.getInputStream());
		while (in.hasNextLine()) {
			log.debug(in.nextLine());
		}

		try {
			Thread.sleep(2000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (this.existsTask(process_name)) {
			log.error("process kill error:" + process_name);
		}

	}
	private boolean existsTask(String process_name) throws IOException {
		Process process = Runtime.getRuntime().exec("tasklist");
	
		Scanner in = new Scanner(process.getInputStream());
		while (in.hasNextLine()) {
			String p = in.nextLine();
			if (p.contains(process_name)) {
				return true;
			}
	
		}
		return false;
	}
	
	/**
	 * run cmd command.
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	public int run(JobExecutionContext context) throws JobExecutionException{
		log.info("["+context.getTrigger().getKey()+"]"+"start run cmd: " + cmd);

        try {
    		this.exectionProcess = Runtime.getRuntime().exec(cmd);
            BufferedInputStream in = new BufferedInputStream(this.exectionProcess.getInputStream());
    		byte[] bytes = new byte[4096];
			while (in.read(bytes) != -1) {}
			log.info("["+context.getTrigger().getKey()+"]"+"start wait cmd end..: " + cmd);
	        int result = this.exectionProcess.waitFor();
	        log.info("["+context.getTrigger().getKey()+"]"+"end cmd execution: " + cmd);
	        return result;
		} catch (Exception e) {
			throw new JobExecutionException(e);
		} 
        
	}
	
	/**
	 * use process.destroy end the process running.
	 */
	public void interupt(){
		log.info("destroy process: " + cmd + "\t ,and process object :" + this.exectionProcess);
		if (this.exectionProcess != null){
			this.exectionProcess.destroy();
			//this.exectionProcess.exitValue();
		}
	}
}

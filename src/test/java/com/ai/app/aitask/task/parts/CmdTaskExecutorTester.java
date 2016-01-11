package com.ai.app.aitask.task.parts;


import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.ai.app.aitask.task.excutor.impl.CmdTaskExecutor;


class WaitProcessClass implements Runnable{
	
	final static private Logger log = Logger.getLogger(WaitProcessClass.class);
	
	private boolean result = false;
	private String process_name = null;
	private long wait_time = 2000l;
	
	public WaitProcessClass(String process_name) {
		super();
		this.process_name = process_name;
	}
	
	

	public WaitProcessClass(String process_name, long wait_time) {
		super();
		this.process_name = process_name;
		this.wait_time = wait_time;
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
	
	private void taskkill(String process_name) throws IOException {
		
		Process process_kill_task = Runtime.getRuntime().exec("taskkill /T /F /IM " + process_name);
		Scanner in = new Scanner(process_kill_task.getInputStream());
		while (in.hasNextLine()) {
			log.debug(in.nextLine());
		}

		try {
			Thread.sleep(1000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (this.existsTask(process_name)) {
			log.error("process kill error:" + process_name);
		}

	}
	
	@Override
    public void run() {
		try {
			Thread.sleep(wait_time);
			this.result = this.existsTask(this.process_name);
			taskkill(this.process_name);
		} catch (Exception e) {
			log.error(e);
		}	
	}
	
}

public class CmdTaskExecutorTester {

	final static private Logger log = Logger.getLogger(CmdTaskExecutorTester.class);
	
	
	@Test(timeout=3000)
	public void testSimpleExecute(){
		CmdTaskExecutor e = new CmdTaskExecutor("ping -n 2 127.0.0.1");
		
		try {
			int ret = e.run(null);
			log.debug(ret);
			Assert.assertTrue(ret == 0);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			Assert.assertTrue("存在报错", false);
		}
		
	}
	
	@Test
	public void testInterrupt(){
		final CmdTaskExecutor e = new CmdTaskExecutor("ping -n 2 127.0.0.1");
		Thread t = new Thread(new Runnable(){
			
			@Override
            public void run() {
				try {
					Thread.sleep(1000l);
					e.interupt();
				} catch (Exception e) {
					e.printStackTrace();
					Assert.assertTrue("存在报错", false);
				}
				
			}
			
		});
		
		try {
			t.start();
			int ret = e.run(null);
			log.debug(ret);
			Assert.assertTrue(ret == 1);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			Assert.assertTrue("存在报错", false);
		}
	}
	
	@Test(timeout=6000)
	public void testNotepadExecute(){
		
		if(System.getProperty("os.name").toLowerCase().indexOf("win")>=0){
			CmdTaskExecutor e = new CmdTaskExecutor("notepad.exe");
			
			try {
				Thread t = new Thread(new WaitProcessClass("notepad.exe"));
				t.start();
				int ret = e.run(null);
				
				
				log.debug(ret);
				Assert.assertTrue(ret == 1); //interupt return 1
				
			} catch (Exception exc) {
				exc.printStackTrace();
				Assert.assertTrue("存在报错", false);
			}
		}
	}
	
	@Test(timeout=5000)
	public void testInterruptNotepadExecute(){
		
		if(System.getProperty("os.name").toLowerCase().indexOf("win")>=0){
			
			final CmdTaskExecutor e = new CmdTaskExecutor("notepad.exe");
			Thread t = new Thread(new Runnable(){
	
				@Override
                public void run() {
					try {
						Thread.sleep(1000l);
						e.interupt();
					} catch (Exception e) {
						e.printStackTrace();
						Assert.assertTrue("存在报错", false);
					}
					
				}
				
			});
			
			try {
				t.start();
				int ret = e.run(null);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	
	@Test(timeout=3000)
	public void testErrorExecute(){
		CmdTaskExecutor e = new CmdTaskExecutor("cmd /c/q d:/run.cmd");
		
		try {
			int ret = e.run(null);
			log.debug(ret);
			Assert.assertTrue(ret == 1);
			Assert.assertFalse("".equals(e.getExecute_out()));
			
		} catch (Exception exc) {
			exc.printStackTrace();
			Assert.assertTrue("存在报错", false);
		}
		
	}
	
	
}

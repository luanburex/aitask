package com.ai.app.aitask.task.parts.impl;

public class BatTaskExecutor extends CmdTaskExecutor{
	
	public BatTaskExecutor(String bat_path, String project_path, String script_path, String result_path){
		super(bat_path + " " + project_path + " " + script_path + " " + result_path);
		
	}

}

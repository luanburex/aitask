package com.ai.app.aitask.exception;


public class TaskParseException extends Exception{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6412953055355052457L;
	
	public TaskParseException(String attr){
		super("[" + attr + "]无法找到或者无法识别。");
	}

}

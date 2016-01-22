package com.ai.app.aitask.task.result;

public class ExecutorProbe {
	String id;
	String name;
	int position = 0;
	long base_line = 0l;
	long warn_time = 0l;
	long timeout_time = 0;
	String desc = "";
	public ExecutorProbe(String id, String name, int position, long base_line,
			long warn_time, long timeout_time, String desc) {
		super();
		this.id = id;
		this.name = name;
		this.position = position;
		this.base_line = base_line;
		this.warn_time = warn_time;
		this.timeout_time = timeout_time;
		this.desc = desc;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public long getBase_line() {
		return base_line;
	}
	public void setBase_line(long base_line) {
		this.base_line = base_line;
	}
	public long getWarn_time() {
		return warn_time;
	}
	public void setWarn_time(long warn_time) {
		this.warn_time = warn_time;
	}
	public long getTimeout_time() {
		return timeout_time;
	}
	public void setTimeout_time(long timeout_time) {
		this.timeout_time = timeout_time;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}

package com.ai.app.aitask.schedule;

import java.util.List;
import java.util.Set;

import com.ai.app.aitask.task.builder.ITaskBuilder;

public interface ITaskScheduler {
    public void start();
    public void shutdown();
    public boolean addTask(ITaskBuilder taskBuilder, boolean replace);
    public Object getTaskState(Object... keys);
    public void fireTask(Object... keys);
    public void suspendTask(Object... keys);
    public Object getTrigger(Object... keys);
    public Set<Object> getTriggers();
    public List<Object> getTasks();
    public String getInfo();//for what?
}

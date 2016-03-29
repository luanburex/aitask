package com.ai.app.aitask.task;

import java.util.Map;

import com.ai.app.aitask.common.Constants;

public interface ITask extends Constants{
    void before(Map<String, Object> trigger, Map<String, Object> datamap);
    void execute(Map<String, Object> trigger, Map<String, Object> datamap);
    void interrupt();
    void after(Map<String, Object> trigger, Map<String, Object> datamap);
}
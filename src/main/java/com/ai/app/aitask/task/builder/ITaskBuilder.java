package com.ai.app.aitask.task.builder;

import java.util.Map;

import com.ai.app.aitask.common.Constants;

public interface ITaskBuilder extends Constants {
    void build();
    Map<String, Object> getTrigger();
    Map<String, Object> getContent();
}

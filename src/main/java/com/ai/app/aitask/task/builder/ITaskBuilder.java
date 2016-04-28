package com.ai.app.aitask.task.builder;

import java.util.Map;

import com.ai.app.aitask.common.Constants;

public interface ITaskBuilder extends Constants {
    ITaskBuilder build();
    Map<String, Object> getTrigger();
    Map<String, Object> getContent();
}

package com.ai.app.aitask.task.excutor;

import java.util.Map;

import com.ai.app.aitask.common.Constants;

public interface IExecutor extends Constants{
    int execute(Map<String, Object> datamap);
    void interrupt();
}

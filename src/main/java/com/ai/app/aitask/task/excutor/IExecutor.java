package com.ai.app.aitask.task.excutor;

import java.util.Map;

public interface IExecutor {
    int execute(Map<String, Object> datamap);
    void interrupt();
}

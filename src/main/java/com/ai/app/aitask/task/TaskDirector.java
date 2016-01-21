package com.ai.app.aitask.task;

import java.util.Map;

import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.task.builder.ITaskBuilder;
import com.ai.app.aitask.task.builder.impl.BatTaskBuilder;
import com.ai.app.aitask.task.builder.impl.CmdTaskBuilder;

public class TaskDirector implements Constants {
    public static ITaskBuilder getBuilder(Map<String, Object> data, String type) throws Exception {
        ITaskBuilder builder = null;
        switch (Integer.parseInt(type)) {
        case TASK_TYPE_BAT:
            builder = new BatTaskBuilder();
        case TASK_TYPE_CMD:
            builder = new CmdTaskBuilder();
        }
        if (null != builder) {
            builder.parseTask(data);
            builder.build();
        }
        return builder;
    }
}

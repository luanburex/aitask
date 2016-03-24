package com.ai.app.aitask.task.builder;

import java.util.Map;

import com.ai.app.aitask.common.Constants;

public interface ITaskBuilder extends Constants {
    /** Rebuild the task struct */
    void parse(Map<String, Object> datamap);
    /** create ContentMap & TriggerMap */
    void build();
    /**
     * Get e.g. Key, Group, CronTime
     * 
     * @return trigger in {@link Map}
     */
    Map<String, Object> getTrigger();
    /**
     * Get e.g. Specific Job Content Data
     * 
     * @return content in {@link Map}
     */
    Map<String, Object> getContent();
}

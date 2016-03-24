package com.ai.app.aitask.task.preparer;

import java.util.Map;

import com.ai.app.aitask.task.ITask;

public interface IDataPreparer {
    /***
     * do prepare Job before {@link ITask} running.
     * e.g. DataFile Creating
     * 
     * @param datamap
     */
    void prepare(Map<String, Object> datamap);
}

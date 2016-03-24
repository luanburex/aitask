package com.ai.app.aitask.task.result;

import java.util.Map;

public interface IResultFetcher {
    public Map<String, Object> fetch(Map<String, Object> datamap);
    public Map<String, Object> error(Map<String, Object> datamap, Exception exception);
}

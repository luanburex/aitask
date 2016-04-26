package com.ai.app.aitask.task.parser;

import java.util.List;
import java.util.Map;

public interface ITaskParser {
    public List<Map<String, Object>> parseContent(String content);
}

package com.ai.app.aitask.task.result.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.FileUtil;
import com.ai.app.aitask.task.result.IResultFetcher;

/**
 * @author renzq
 * @author Alex Xu
 */
public class DefaultResultFetcher implements IResultFetcher {

    private Map<String, Object> exedata;

    public DefaultResultFetcher(Map<String, Object> exedata) {
        this.exedata = exedata;
    }
    @Override
    public Map<String, Object> fetch(Map<String, Object> datamap) {
        String pathWorkspace = (String) exedata.get("pathWorkspace");
        String pathExedata = (String) exedata.get("pathExedata");
        String pathScript = (String) exedata.get("pathScript");
        String pathResult = (String) exedata.get("pathResult");
        String pathLog = (String) exedata.get("pathLog");

        Config exefile = Config.instance(pathExedata);

        Map<String, Object> resultMap = new HashMap<String, Object>();
        String result = FileUtil.readFile(pathResult);
        resultMap.put("cslog", result.replaceAll("\r\n", "\n"));
        return resultMap;
    }
    @Override
    public Map<String, Object> error(Map<String, Object> datamap, Exception exception) {
        // TODO Auto-generated method stub
        exception.getMessage();
        return null;
    }
}

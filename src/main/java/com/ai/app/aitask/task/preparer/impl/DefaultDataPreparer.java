package com.ai.app.aitask.task.preparer.impl;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.FileUtil;
import com.ai.app.aitask.task.preparer.IDataPreparer;

public class DefaultDataPreparer implements IDataPreparer {

    private Map<String, Object> exedata;

    public DefaultDataPreparer(Map<String, Object> exedata) {
        this.exedata = exedata;
    }

    @Override
    public void prepare(Map<String, Object> datamap) {
        String pathWorkspace = (String) exedata.get("pathWorkspace");
        String pathExedata = (String) exedata.get("pathExedata");
        String pathScript = (String) exedata.get("pathScript");
        String pathResult = (String) exedata.get("pathResult");
        String pathLog = (String) exedata.get("pathLog");

        FileUtil.makeFile(new File(pathWorkspace, pathLog), "some logs");

        // The log file
        FileUtil.makeFile(pathLog, "some logs");
        // The result file
        FileUtil.makeFile(pathResult, "some result");
        // The script file
        datamap = Caster.cast(datamap.get("datamap"));
        Map<String, String> scriptmap = Caster.cast(datamap.get("script"));
        FileUtil.makeFile(pathScript, scriptmap.get("script"));

        Config config = Config.instance(pathExedata);
        config.setProperty("task", "log", pathLog);
        config.setProperty("task", "result", pathResult);
        config.setProperty("task", "script", pathScript);
        Map<String, Object> taskmap = Caster.cast(datamap.get("task"));
        for (Entry<String, Object> entry : taskmap.entrySet()) {
            config.setProperty("task", entry.getKey(), String.valueOf(entry.getValue()));
        }
        System.out.println(taskmap);
        Map<String, Map<String, Map<String, Object>>> detailmap;
        detailmap = Caster.cast(datamap.get("detaillist"));
        for (Entry<String, Map<String, Map<String, Object>>> pair : detailmap.entrySet()) {
            for (Entry<String, Map<String, Object>> data : pair.getValue().entrySet()) {
                config.setProperty("data", pair.getKey(), data.getKey());
                for (Entry<String, Object> detail : data.getValue().entrySet()) {
                    config.setProperty(data.getKey(), detail.getKey(),
                            String.valueOf(detail.getValue()));
                }
            }
        }
        config.write();
    }
}

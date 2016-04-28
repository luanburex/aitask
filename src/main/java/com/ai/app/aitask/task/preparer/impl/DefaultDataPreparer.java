package com.ai.app.aitask.task.preparer.impl;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.FileUtil;
import com.ai.app.aitask.task.preparer.IDataPreparer;

/**
 * @author Alex Xu
 */
public class DefaultDataPreparer implements IDataPreparer {
    protected static final Logger logger = Logger.getLogger(DefaultDataPreparer.class);
    private Map<String, Object>   exedata;

    @Override
    public void prepare(Map<String, Object> datamap) {
        String pathExedata = (String) exedata.get("pathExedata");
        String pathScript = (String) exedata.get("pathScript");
        String pathResult = (String) exedata.get("pathResult");
        String pathLog = (String) exedata.get("pathLog");

        FileUtil.makeFile(pathLog, "some logs");
        // The log file
        FileUtil.makeFile(pathLog, "some logs");
        // The result file
        FileUtil.makeFile(pathResult, "some result");
        // The script file
        datamap = Caster.cast(datamap.get("datamap"));
        Map<String, String> scriptmap = Caster.cast(datamap.get("script"));
        FileUtil.makeFile(pathScript, scriptmap.get("script"));

        new File(pathExedata).delete();
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();

        datamap.put("log", pathLog);
        datamap.put("result", pathResult);
        datamap.put("script", pathScript);
        FileUtil.makeFile(pathExedata, gson.toJson(datamap));
    }
}

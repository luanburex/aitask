package com.ai.app.aitask.task.builder.impl;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.task.builder.AbstractTaskBuilder;
import com.ai.app.aitask.task.excutor.impl.DefaultTaskExecutor;
import com.ai.app.aitask.task.preparer.impl.DefaultDataPreparer;
import com.ai.app.aitask.task.result.impl.DefaultResultFetcher;

public class DefaultTaskBuilder extends AbstractTaskBuilder {

    @Override
    public void parse(Map<String, Object> datamap) {
        TEMP: { // TODO Temp
            Map<String, Object> task = Caster.cast(datamap.get("task"));
            Map<String, Object> plan = Caster.cast(datamap.get("plan"));
            task.put("cron", plan.get("cron"));
            Calendar c = Calendar.getInstance();
            String t = c.get(Calendar.SECOND) + " " + c.get(Calendar.MINUTE) + " * * * ?";
            c.setTime(new Date(System.currentTimeMillis() + 2000l));
            t = c.get(Calendar.SECOND) + " " + c.get(Calendar.MINUTE) + " * * * ?";
            // TODO Delay 2 second
            task.put("cron", t);
            task.put("instant", "1".equals(plan.get("ifInstance")));//1/0
            // TODO Missing properties
            task.put("timeout", "-1");
            task.put("taskGroup", "AITASK");
            //            task.put("taskCategory", Integer.toString(TASK_TYPE_CMD));
        }
        super.parse(datamap);
    }

    @Override
    public void build() {
        {
            Map<String, Object> task = Caster.cast(datamap.get("task"));
            //            Map<String, Object> plan = Caster.cast(datamap.get("plan"));
            key.put("key", task.get("taskId"));
            key.put("cron", task.get("cron"));
            key.put("group", task.get("taskGroup"));
            key.put("timeout", task.get("timeout"));
            key.put("instant", task.get("instant"));
            content.put("datamap", datamap);
        }
        Config config = Config.instance("aitask.properties");
        Map<String, Object> exedata = new HashMap<String, Object>();

        String pathWorkspace = config.getProperty(null, "workspace");
        String pathExedata = new File(pathWorkspace, "exe").getAbsolutePath();
        String pathScript = new File(pathWorkspace, "scr").getAbsolutePath();
        String pathResult = new File(pathWorkspace, "rst").getAbsolutePath();
        String pathLog = new File(pathWorkspace, "log").getAbsolutePath();

        exedata.put("pathExedata", pathExedata);
        exedata.put("pathScript", pathScript);
        exedata.put("pathResult", pathResult);
        exedata.put("pathLog", pathLog);
        preparer = new DefaultDataPreparer(exedata);
        executor = new DefaultTaskExecutor("net", "user");
        fetcher = new DefaultResultFetcher(exedata);
        super.build();
    }
}

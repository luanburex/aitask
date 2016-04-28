package com.ai.app.aitask.task.builder.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.task.builder.ITaskBuilder;
import com.ai.app.aitask.task.executor.IExecutor;
import com.ai.app.aitask.task.executor.impl.ProcessTaskExecutor;

/**
 * @author renzq
 * @author Alex Xu
 */
public class ProcessTaskBuilder extends AbstractTaskBuilder {
    public ProcessTaskBuilder(Map<String, Object> datamap, Map<String, String> properties) {
        super(datamap, properties);
        delay2();
    }

    private void delay2() { //TODO delay for 2 second
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

    @Override
    public ITaskBuilder build() {
        Config config = Config.instance(CONFIG_AITASK);
        Map<String, Object> task = Caster.cast(datamap.get("task"));
        //            Map<String, Object> plan = Caster.cast(datamap.get("plan"));
        key.put("key", task.get("taskId"));
        key.put("cron", task.get("cron"));
        key.put("group", task.get("taskGroup"));
        key.put("timeout", task.get("timeout"));
        key.put("instant", task.get("instant"));
        content.put("datamap", datamap);
        return super.build();
    }
}

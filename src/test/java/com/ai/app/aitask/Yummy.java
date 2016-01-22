package com.ai.app.aitask;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Mapper;
import com.ai.app.aitask.common.Processor;
import com.ai.app.aitask.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Yummy {

    public static void main(String[] args) throws Exception  {
        Map<String, List<Map<String, String>>> m = Caster.cast(Mapper.parseJSON(FileUtils.readFileInClasspath("result_schedule_001.json")));
        for(Entry<String, String> entry : m.get("steplog").get(0).entrySet()) {
            System.out.println("stepmap.put(\""+entry.getKey()+"\", \"\");");
        }
    }
}

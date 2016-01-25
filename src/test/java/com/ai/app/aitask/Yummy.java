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
import com.ai.app.aitask.common.ProcessWorker;
import com.ai.app.aitask.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Yummy {

    public static void main(String[] args) throws Exception  {
        Process p = Runtime.getRuntime().exec("tasklist");
        System.out.println(p.toString());
    }
}

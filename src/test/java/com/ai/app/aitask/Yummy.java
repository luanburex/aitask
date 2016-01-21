package com.ai.app.aitask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Mapper;
import com.ai.app.aitask.utils.FileUtils;

public class Yummy {

    public static void main(String[] args) throws Exception  {
        String cron = "42 51 * * * ?";
        CronExpression e = new CronExpression(cron);
        System.out.println(CronExpression.isValidExpression("42 51 * * * ?"));
        System.out.println(CronScheduleBuilder.cronSchedule(cron).build().getFireTimeAfter(new Date()));
    }
}

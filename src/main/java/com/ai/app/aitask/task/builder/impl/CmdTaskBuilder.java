package com.ai.app.aitask.task.builder.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.FileUtil;
import com.ai.app.aitask.task.builder.AbstractTaskBuilder;
import com.ai.app.aitask.task.excutor.impl.CmdTaskExecutor;
import com.ai.app.aitask.task.result.impl.TestResultFetcher;
import com.google.gson.GsonBuilder;

public class CmdTaskBuilder extends AbstractTaskBuilder {

    protected final static Logger log = Logger.getLogger(CmdTaskBuilder.class);

    @Override
    public void parseTask(Map<String, Object> datamap) throws Exception {
        // 生成任务执行所需要的文件
        //        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(datamap));

        // 执行的数据文件
        String datapath = "exedata";
        Config config = Config.instance("exedata");

        // 这个是日志文件
        String logpath = "logpath.txt";
        FileUtil.makeFile(logpath, "这里放日志");
        config.setProperty("task", "log", logpath);

        // 这个是结果文件
        String resultpath = "resultpath.txt";
        FileUtil.makeFile(resultpath, "这里放结果");
        config.setProperty("task", "result", resultpath);

        Map<String, String> scriptmap = Caster.cast(datamap.get("script"));
        // 这个是脚本文件
        String scriptpath = "script.txt";
        FileUtil.makeFile(scriptpath, scriptmap.get("script"));
        config.setProperty("task", "script", scriptpath);

        Map<String, String> taskmap = Caster.cast(datamap.get("task"));
        for (Entry<String, String> entry : taskmap.entrySet()) {
            config.setProperty("task", entry.getKey(), entry.getValue());
        }

        Map<String, Object> detailmap = Caster.cast(datamap.get("data"));
        List<Map<String, String>> detaillist = Caster.cast(detailmap.get("detail"));
        for (int i = 0, sum = detaillist.size(); i < sum; i++) {
            for (Entry<String, String> entry : detaillist.get(i).entrySet()) {
                config.setProperty("detail" + i, entry.getKey(), entry.getValue());
            }
        }
        config.setProperty("task", "detail", Integer.toString(detaillist.size()));

        config.write();

        jobDatamap.put("preparer", null);
        String[] cmd_str = { "ping", "www.baidu.com" };
        // TODO 这里怎么执行先不管了，继续看Executor里的东西
        //        cmd_str = new String[]{"ruby", "aitask.rb", new File(datapath).getAbsolutePath()};
        jobDatamap.put("executor", new CmdTaskExecutor(cmd_str));
        jobDatamap.put("result", new TestResultFetcher(resultpath, logpath));
        super.parseTask(datamap);
    }
}

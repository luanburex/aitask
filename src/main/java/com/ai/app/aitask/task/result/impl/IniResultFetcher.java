package com.ai.app.aitask.task.result.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.OrderedProperties;
import com.ai.app.aitask.task.result.ExecutorProbe;
import com.ai.app.aitask.task.result.IResultFetcher;

public class IniResultFetcher implements IResultFetcher {

    transient final static protected Logger log         = Logger.getLogger(IniResultFetcher.class);
    String                                result_path = null;

    public IniResultFetcher(String result_path) {
        this.result_path = result_path;
    }

    @Override
    public String fetch(JobExecutionContext context) throws JobExecutionException {
        // String project_path = context.getMergedJobDataMap().getString("project_path");

        // Read ini file get result
        // String result_path = context.getMergedJobDataMap().getString("ini_path");
        // if(result_path == null || result_path.isEmpty())
        // result_path = context.getMergedJobDataMap().getString("result_path");
        // log.info("Read result: " + result_path);

        Config file = Config.instance(this.result_path);

        OrderedProperties result_section = file.getProperties("探测结果");
        if (result_section == null)
            result_section = new OrderedProperties();
        String data_id = result_section.getProperty("数据ID", "");
        String data_value = result_section.getProperty("数据值", "");
        String data_desc = result_section.getProperty("数据描述", "");
        String start_time_str = result_section.getProperty("开始时间", "");
        String end_time_str = result_section.getProperty("结束时间", "");
        String result = result_section.getProperty("执行结果", "");
        String eclapse = result_section.getProperty("累计耗时", "");
        String rst_log = result_section.getProperty("日志", "");
        String key_eclapse = result_section.getProperty("关键耗时", "");

        // write to xml
        Element root = DocumentHelper.createElement("ResultCase");
        root.addAttribute("task_id", context.getMergedJobDataMap().getString("task_id"));
        root.addAttribute("run_id", context.getMergedJobDataMap().getString("task_id"));
        root.addAttribute("case_id", context.getMergedJobDataMap().getString("case_id"));
        root.addAttribute("case_name", context.getMergedJobDataMap().getString("case_name"));
        root.addAttribute("case_category", context.getMergedJobDataMap().getString("ctype"));
        root.addAttribute("case_module", context.getMergedJobDataMap().getString("case_module"));
        root.addAttribute("group_no", context.getMergedJobDataMap().getString("group_no"));
        root.addAttribute("group_name", context.getMergedJobDataMap().getString("group_name"));
        root.addAttribute("agent_id", context.getMergedJobDataMap().getString("agent_id"));
        root.addAttribute("agent_name", context.getMergedJobDataMap().getString("agent_name"));
        root.addAttribute("data_id", data_id);
        root.addAttribute("data_value", data_value);
        root.addAttribute("data_desc", data_desc);
        root.addAttribute("start_time",
                start_time_str == null || start_time_str.isEmpty() ? new SimpleDateFormat(
                        "yyyy/MM/dd HH:mm:ss").format(new Date()) : start_time_str);
        root.addAttribute("end_time",
                end_time_str == null || end_time_str.isEmpty() ? new SimpleDateFormat(
                        "yyyy/MM/dd HH:mm:ss").format(new Date()) : end_time_str);

        root.addAttribute("rst_log", rst_log == null ? "日志：" : rst_log);
        root.addAttribute("eclapse", eclapse);
        root.addAttribute("key_eclapse", key_eclapse);
        root.addAttribute("result", result);

        @SuppressWarnings("unchecked")
        List<ExecutorProbe> probes = (List<ExecutorProbe>) context.getMergedJobDataMap().get(
                "probes");
        if (probes != null) {
            Element steps = root.addElement("steps");
            for (ExecutorProbe p : probes) {
                String result_str = result_section.getProperty(p.getName());

                if (result_str == null || result_str.isEmpty()) {
                    root.addAttribute("rst_log", root.attributeValue("rst_log") + "\n"
                            + "未发现探测点或步骤：" + p.getName());
                    continue;
                }

                String[] result_list = result_str.split(",");
                String p_result = result_list[0].trim();
                String p_time = result_list[1].trim();
                String p_log = result_list[2].trim();

                Element step = steps.addElement("ResultStep");
                step.addAttribute("step_id", p.getId());
                step.addAttribute("step_name", p.getName());
                step.addAttribute("step_desc", p.getDesc());
                step.addAttribute("expect_result", "");

                if (!result_str.matches("^.*,.*,.*$")) {
                    // root.setAttributeValue("rst_log", root.attributeValue("rst_log") + "\n" + "发现探测点或步骤的输出格式有误" +
                    // p.getName() + ":\t" + result_str);
                    // continue;

                    step.addAttribute("start_time", "");
                    step.addAttribute("eclapse", "0");
                    step.addAttribute("result", "1");
                    step.addAttribute("rst_log", "发现探测点或步骤的输出格式有误" + p.getName() + ":\t"
                            + result_str);
                } else {
                    step.addAttribute("start_time", "");
                    step.addAttribute("eclapse", p_time);
                    step.addAttribute("result", p_result);
                    step.addAttribute("rst_log", p_log);
                }
            }

        }
        try {
            String result_save_url = Config.instance("client.properties").getProperty(null,
                    "aitask.result.url");
            if (result_save_url == null)
                throw new Exception("aitask.result.save.url not found");
            // HttpClient.post(result_save_url, root.asXML(), "text/xml");
        } catch (Exception e) {
            log.error(e);
        }
        return root.asXML();
    }

    @Override
    public String error(JobExecutionContext context, JobExecutionException exception)
            throws JobExecutionException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Element root = DocumentHelper.createElement("ResultCase");
        root.addAttribute("task_id", context.getMergedJobDataMap().getString("task_id"));
        root.addAttribute("run_id", context.getMergedJobDataMap().getString("task_id"));
        root.addAttribute("case_id", context.getMergedJobDataMap().getString("case_id"));
        root.addAttribute("case_name", context.getMergedJobDataMap().getString("case_name"));
        root.addAttribute("case_category", context.getMergedJobDataMap().getString("ctype"));
        root.addAttribute("case_module", context.getMergedJobDataMap().getString("case_module"));
        root.addAttribute("group_no", context.getMergedJobDataMap().getString("group_no"));
        root.addAttribute("group_name", context.getMergedJobDataMap().getString("group_name"));
        root.addAttribute("agent_id", context.getMergedJobDataMap().getString("agent_id"));
        root.addAttribute("agent_name", context.getMergedJobDataMap().getString("agent_name"));
        root.addAttribute("data_id", "");
        root.addAttribute("data_value", "");
        root.addAttribute("data_desc", "");
        root.addAttribute("start_time", format.format(new Date()));
        root.addAttribute("end_time", format.format(new Date()));

        root.addAttribute("rst_log", exception.getMessage());
        root.addAttribute("eclapse", "0");
        root.addAttribute("key_eclapse", "0");
        root.addAttribute("result", "1");
        try {
            Config config = Config.instance("agent.properties");
            String result_save_url = config.getProperty(null, "aitask.result.save.url");
            if (result_save_url == null)
                throw new Exception("aitask.result.save.url not found");
            // HttpClient.post(result_save_url, root.asXML(), "text/xml");
        } catch (Exception e) {
            log.error(e);
        }
        return root.asXML();
    }

}

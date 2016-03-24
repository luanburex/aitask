package com.ai.app.aitask.task.builder.impl;

public class OldBat {
    import java.util.Map;

    import org.apache.log4j.Logger;

    import com.ai.app.aitask.common.Caster;
    import com.ai.app.aitask.task.builder.AbstractTaskBuilder;
    import com.ai.app.aitask.task.excutor.impl.BatTaskExecutor;
    import com.ai.app.aitask.task.result.impl.IniResultFetcher;

    public class BatTaskBuilder extends AbstractTaskBuilder {

        protected final static Logger log = Logger.getLogger(BatTaskBuilder.class);

        @Override
        public void parseTask(Map<String, Object> datamap) throws Exception {
            // TODO where do I find those pathS ?
            Map<String, Object> taskData = Caster.cast(datamap.get("task"));
            String path_bat = (String) taskData.get("bat_path");
            String path_ini = (String) taskData.get("ini_path");
            String path_script = (String) taskData.get("script_path");
            String path_project = (String) taskData.get("project_path");

            BatTaskExecutor exe = new BatTaskExecutor(path_bat, path_project, path_script, path_ini);
            IniResultFetcher fetcher = new IniResultFetcher(path_ini);

            jobDatamap.put("preparer", null);
            jobDatamap.put("executor", exe);
            jobDatamap.put("result", fetcher);
            super.parseTask(datamap);
        }
    }
}

package com.ai.app.aitask.schedule;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import com.ai.app.aitask.listener.quartz.ScheduleListner;
import com.ai.app.aitask.listener.quartz.ExecuteListener;
import com.ai.app.aitask.listener.quartz.TimeoutListener;
import com.ai.app.aitask.task.builder.ITaskBuilder;

public class CopyOfTaskScheduler {

    protected final static Logger log = Logger.getLogger(CopyOfTaskScheduler.class);

    /**
     * the base scheduler of Quartz
     */
    private Scheduler                     instance;

    public Scheduler getScheduler() {
        return this.instance;
    }

    /**
     * the Constructor of the taskschedule:get properties and set listeners.
     *
     * @param scheduler
     * @throws IOException
     * @throws SchedulerException
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public CopyOfTaskScheduler(Scheduler scheduler) throws IOException, SchedulerException, SQLException,
    InstantiationException, IllegalAccessException, ClassNotFoundException {

        log.debug("[" + scheduler.getSchedulerName() + "]" + "set the Scheduler.");
        this.instance = scheduler;

        log.debug("[" + scheduler.getSchedulerName() + "]" + "add the Scheduler listener.");
        scheduler.getListenerManager().addSchedulerListener(new ScheduleListner());

        log.debug("[" + scheduler.getSchedulerName() + "]" + "add listeners to all task.");

        // scheduler.getListenerManager().addJobListener(new BeforeAndAfterTaskListener(),
        // EverythingMatcher.allJobs());
        scheduler.getListenerManager().addJobListener(new ExecuteListener());
        scheduler.getListenerManager().addJobListener(new TimeoutListener());
        scheduler.start();
    }
    
    public void fetch() throws Exception{
        String url = AgentProperties.getInstance().getProperty("aitask.task.sync.url");
        String agent_name = AgentProperties.getInstance().getProperty("aitask.name", AgentProperties.getInstance().getProperty("org.quartz.scheduler.instanceName"));
        
        url = url.trim();
        agent_name = agent_name.trim();
        String task_xml = HttpClient.post(url, "agent_name=" + agent_name, "x-www-form-urlencoded");
        System.out.println(task_xml);
        
        Document document = DocumentHelper.parseText(task_xml);
        Element root = document.getRootElement();
        
        Iterator<Element> elements = root.elementIterator("task");
        while(elements.hasNext()){
            Element current_element = elements.next();
            ITaskBuilder tb = TaskDirector.generateTaskBuilderByXml(current_element.asXML());
            if(TriggerState.BLOCKED.equals(this.ts.getTaskState(tb.getTrigger().getKey()))){
                log.debug("task is BLOCKED, add to sync queue." + tb.getTrigger().getKey());
                this.task_sync.putTask(tb.getTrigger().getKey(), current_element.asXML());
            }else{
                log.debug("add task:" + tb.getTrigger().getKey());
                this.ts.addTask(tb, true);
            }
        }
    }
    

    public void addTask(ITaskBuilder taskBuilder, boolean replace) throws Exception {

        JobDetail job = taskBuilder.getContent();
        Trigger trigger = taskBuilder.getAuth();

        log.info("[" + this.instance.getSchedulerName() + "]" + "add the task ["
                + trigger.getKey().toString() + "]");

        instance.scheduleJob(job, trigger);

        log.debug("[" + this.instance.getSchedulerName() + "]there is "
                + instance.getCurrentlyExecutingJobs().size() + " jobs running.");

        log.info("[" + this.instance.getSchedulerName() + "]the trigger [" + trigger.getKey()
                + "] " + "will start at" + trigger.getNextFireTime() + " and trigger state is "
                + this.instance.getTriggerState(trigger.getKey()).toString());

    }
    public TriggerState getTaskStateByTrigger(TriggerKey triggerKey) throws SchedulerException {
        return instance.getTriggerState(triggerKey);
    }

    public TriggerState getTaskState(TriggerKey triggerKey) throws SchedulerException {
        return this.getTaskStateByTrigger(triggerKey);
    }

    public void interruptByTrigger(TriggerKey triggerKey) throws SchedulerException {

        JobKey jobKey = this.getScheduler().getTrigger(triggerKey).getJobKey();
        instance.interrupt(jobKey);
    }

    public void fireTrigger(TriggerKey triggerkey) throws SchedulerException {

        Trigger trigger = this.instance.getTrigger(triggerkey);
        this.instance.triggerJob(trigger.getJobKey(), trigger.getJobDataMap());
    }

    public Set<TriggerKey> getAllTriggerKey() throws SchedulerException {
        GroupMatcher<TriggerKey> gm = GroupMatcher.anyTriggerGroup();
        Set<TriggerKey> keys = this.instance.getTriggerKeys(gm);
        return keys;
    }

    public String getTrigerInfo() throws SchedulerException {
        StringBuffer sb = new StringBuffer();
        for (TriggerKey key : this.getAllTriggerKey()) {
            Trigger trigger = this.instance.getTrigger(key);
            sb.append(trigger.getKey());
            sb.append("\t");
            sb.append(instance.getTriggerState(key));
            sb.append("\t");
            sb.append(trigger.getPreviousFireTime());
            sb.append("\t");
            sb.append(trigger.getNextFireTime());
            sb.append("\n");
        }
        return sb.toString();
    }

    public List<JobExecutionContext> getTasks() {
        try {
            return instance.getCurrentlyExecutingJobs();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return new ArrayList<JobExecutionContext>();
    }
}

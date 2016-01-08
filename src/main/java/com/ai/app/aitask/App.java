package com.ai.app.aitask;

import com.ai.app.aitask.common.ConfigurationFile;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.deamon.TaskSyncDaemon;
import com.ai.app.aitask.net.Client;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        ConfigurationFile config = new ConfigurationFile("client.properties");
        new Client(config).start();
        try {
            ScheduleDaemon.instance().start();
//            new TaskSyncDaemon(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

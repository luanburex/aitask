package com.ai.app.aitask;

import com.ai.app.aitask.common.Configuration;
import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.deamon.TaskSyncDaemon;
import com.ai.app.aitask.net.Client;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        new Client().start();
        try {
            ScheduleDaemon.instance().start();
            new TaskSyncDaemon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.ai.app.aitask.schedule;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ai.app.aitask.deamon.TaskSyncDaemon;
import com.ai.app.aitask.utils.TestJettyServer;

public class TaskSyncTester {

	static TestJettyServer server = null;
	@BeforeClass
	public static void startup() throws Exception{
		server = new TestJettyServer();
		server.start();
	}
	@AfterClass
	public static void teardown() throws Exception{
		if (server == null)
			server.stop();
	}
	@Test
	public void testNormal() throws IOException{
		TaskFetch.fetch();
	}
}

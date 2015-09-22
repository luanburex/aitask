package com.ai.app.aitask.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AgentProperties {
	private static Properties instance = null;

	protected AgentProperties() throws IOException {
		String filename = System.getProperty("user.dir")
				+ "/agent.properties";
		InputStream in = new BufferedInputStream(new FileInputStream(filename));
		Properties properties = new Properties();
		properties.load(in);
		instance = properties;
	}

	public static synchronized Properties getInstance() throws IOException {

		if (instance == null) {
			new AgentProperties();
		}
		return instance;
	}
}

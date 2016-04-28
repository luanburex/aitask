package com.ai.app.aitask.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface Constants {
    String DEFAULT_CHARSET = "GBK";
    String LINE_SEPARATOR  = System.getProperty("line.separator");

    String CONFIG_CLIENT   = "client.properties";
    String CONFIG_AITASK   = "aitask.properties";
    String CONFIG_QUARTZ   = "quartz.properties";

    Gson   GSON            = new GsonBuilder().setPrettyPrinting().create();
}

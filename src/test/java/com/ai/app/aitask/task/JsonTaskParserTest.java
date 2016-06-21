package com.ai.app.aitask.task;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ai.app.aitask.common.FileUtil;
import com.ai.app.aitask.task.parser.ITaskParser;
import com.ai.app.aitask.task.parser.impl.JsonTaskParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonTaskParserTest {
    private static Gson gson;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        ITaskParser parser = new JsonTaskParser();
        String taskContent = FileUtil.readFile("task_schedule_001.json");
        List<Map<String, Object>> parsedContent = parser.parseContent(taskContent);

        System.out.println(gson.toJson(parsedContent));
    }
}

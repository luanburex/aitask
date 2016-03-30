package com.ai.app.aitask;

import java.util.Arrays;

import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.common.OrderedProperties;


public class Yummy implements Constants{
    public static void main(String[] args) throws Exception {
        String path = "C:\\workspace\\aitaskdev\\exe";
        path = path.replaceAll("\\\\", "\\\\\\\\");
        System.out.println("a".replaceAll("a", path));
    }
}

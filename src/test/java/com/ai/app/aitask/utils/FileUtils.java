package com.ai.app.aitask.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static String readXmlFileInClasspath(String filename) {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.out.println(path);
        return FileUtils.readFileToString(path.concat(filename));

    }

    public static String readFileToString(String fileName) {
        StringBuffer sb = new StringBuffer();

        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new java.io.FileReader(file));
            String tempString = null;
            int line = 1;

            while ((tempString = reader.readLine()) != null) {
                sb.append(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    return "";
                }
            }
            return sb.toString();
        }
    }
}

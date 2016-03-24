package com.ai.app.aitask.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class FileUtil {
    public static void makeFile(String path, String content) {
        makeFile(new File(path), content);
    }

    public static void makeFile(File file, String content) {
        makeFile(file, content, true);
    }

    public static void makeFile(String path, String content, Boolean overwrite) {
        makeFile(new File(path), content, true);
    }

    public static void makeFile(File file, String content, Boolean overwrite) {
        try {
            if (file.exists()) {
                if (overwrite) {
                    file.delete();
                    file.createNewFile();
                } else {
                    return;
                }
            }
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String path) {
        return readFile(path, "UTF-8");
    }

    public static String readFile(String path, String charset) {
        return readFile(readReader(path, charset));
    }

    public static String readFile(Reader reader) {
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            buffer.append(null == line ? "" : line);
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(System.getProperty("line.separator")).append(line);
            }
            bufferedReader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static Reader readReader(String path) {
        return readReader(path, "UTF-8");
    }

    public static Reader readReader(String path, String charset) {
        try {
            return new InputStreamReader(readStream(path), charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream readStream(String path) {
        File file = new File(path);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(path + " not found");
        }
        return null;
    }
}

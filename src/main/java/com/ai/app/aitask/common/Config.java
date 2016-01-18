package com.ai.app.aitask.common;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class Config {

    protected final static Logger                      log = Logger.getLogger(Config.class);
    protected transient static Map<String, Config>     files;
    protected transient OrderedProperties              current;
    protected transient OrderedProperties              builtin;
    protected transient Map<String, OrderedProperties> sections;
    protected transient String                         filename;
    protected transient String                         currentSecion;
    static {
        files = new HashMap<String, Config>();
    }

    public static synchronized Config instance(String filename) {
        Config config;
        if (files.containsKey(filename)) {
            config = files.get(filename);
        } else {
            files.put(filename, config = new Config(filename));
        }
        return config;
    }

    protected Config(String filename) {
        this.builtin = new OrderedProperties();
        this.sections = new HashMap<String, OrderedProperties>();
        this.filename = filename;
        InputStream input = ClassLoader.getSystemResourceAsStream(filename);
        if (null == input) {
            try {
                throw new FileNotFoundException("resource not found");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                for (String line = reader.readLine(); null != line; line = reader.readLine()) {
                    // log.debug(line);
                    parseLine(line);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void parseLine(String line) {
        line = line.trim();
        if (line.isEmpty()) {
        } else if ('#' == line.charAt(0)) {
        } else if (line.matches(".?\\[.*\\]")) {
            currentSecion = line.replaceFirst(".?\\[(.*)\\]", "$1");
            current = new OrderedProperties();
            sections.put(currentSecion, current);
        } else if (line.matches(".*=.*")) {
            if (current == null) {
                current = builtin;
            }
            int i = line.indexOf('=');
            String name = line.substring(0, i).trim();
            String value = line.substring(i + 1).trim();
            current.setProperty(name, value);
        }
    }
    public String[] getSections() {
        ArrayList<String> section_arraylist = new ArrayList<String>();
        for (String key : sections.keySet()) {
            section_arraylist.add(key);
        }
        return section_arraylist.toArray(new String[section_arraylist.size()]);
    }

    public String getProperty(String section, String name) {
        OrderedProperties p = getProperties(section);
        if (null == p) {
            return null;
        } else {
            String value = p.getProperty(name);
            return value;
        }
    }

    public OrderedProperties getProperties(String section) {
        return null == section ? builtin : sections.get(section);
    }

    public void setProperty(String section, String name, String value) {
        OrderedProperties properties;
        if (null == section) {
            properties = builtin;
        } else if (!sections.containsKey(section)) {
            sections.put(section, properties = new OrderedProperties());
        } else {
            properties = sections.get(section);
        }
        properties.setProperty(name, value);
    }

    public void write() {
        BufferedOutputStream stream = null;
        OutputStreamWriter writer = null;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(this.filename));
            writer = new OutputStreamWriter(stream, "UTF-8");
            // TODO linux 文件头?
            // stream.write(Integer.parseInt("EF", 16));
            // stream.write(Integer.parseInt("BB", 16));
            // stream.write(Integer.parseInt("BF", 16));
            {
                for (String key : builtin.stringPropertyNames()) {
                    writer.write(key + "=" + builtin.getProperty(key) + "\r\n");
                }
            }
            for (String section : sections.keySet()) {
                writer.write("\r\n");
                writer.write("[" + section + "]");
                for (String key : sections.get(section).stringPropertyNames()) {
                    writer.write("\r\n" + key + "=" + sections.get(section).getProperty(key));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

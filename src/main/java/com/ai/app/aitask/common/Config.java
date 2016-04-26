package com.ai.app.aitask.common;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author renzq
 * @author Alex Xu
 */
public class Config {
    protected static final Logger            logger         = Logger.getLogger(Config.class);
    protected static Map<String, Config>     configCache = new HashMap<String, Config>();
    protected OrderedProperties              builtinSection;
    protected OrderedProperties              cursorSection;
    protected Map<String, OrderedProperties> sectionMap;
    protected String                         cursorTag;
    protected String                         fileName;

    public static synchronized Config instance(String filename) {
        if (!configCache.containsKey(filename)) {
            configCache.put(filename, new Config(filename));
            logger.debug(String.format("config %s cached", filename));
        }
        return configCache.get(filename);
    }

    private Config(String filename) {
        this.builtinSection = new OrderedProperties();
        this.sectionMap = new HashMap<String, OrderedProperties>();
        this.fileName = filename;
        InputStream input = ClassLoader.getSystemResourceAsStream(filename);
        if (null == input) {
            try {
                input = new FileInputStream(filename);
            } catch (FileNotFoundException e) {
                try {
                    new File(filename).createNewFile();
                    input = new FileInputStream(filename);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            for (String line = reader.readLine(); null != line; line = reader.readLine()) {
                parseLine(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseLine(String line) {
        line = line.trim();
        if (line.isEmpty() || '#' == line.charAt(0)) {
            // let it
        } else if (line.matches(".?\\[.*\\]")) {
            cursorTag = line.replaceFirst(".?\\[(.*)\\]", "$1");
            cursorSection = new OrderedProperties();
            sectionMap.put(cursorTag, cursorSection);
        } else if (line.matches(".*=.*")) {
            if (cursorSection == null) {
                cursorSection = builtinSection;
            }
            int i = line.indexOf('=');
            String name = line.substring(0, i).trim();
            String value = line.substring(i + 1).trim();
            cursorSection.setProperty(name, value);
        }
    }

    public String[] getSectionTags() {
        return sectionMap.keySet().toArray(new String[sectionMap.size()]);
    }

    public String getProperty(String propertyName) {
        return getProperty(null, propertyName);
    }

    public String getProperty(String sectionTag, String propertyName) {
        OrderedProperties section = getProperties(sectionTag);
        return null == section ? null : section.getProperty(propertyName);
    }

    public OrderedProperties getProperties(String section) {
        return null == section ? builtinSection : sectionMap.get(section);
    }

    public void setProperty(String sectionTag, String propertyName, String value) {
        OrderedProperties section;
        if (null == sectionTag) {
            section = builtinSection;
        } else if (!sectionMap.containsKey(sectionTag)) {
            sectionMap.put(sectionTag, section = new OrderedProperties());
        } else {
            section = sectionMap.get(sectionTag);
        }
        section.setProperty(propertyName, value);
    }

    public void write() {
        BufferedOutputStream stream = null;
        OutputStreamWriter writer = null;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(this.fileName));
            writer = new OutputStreamWriter(stream, "UTF-8");
            /*
             * TODO linux header
             * stream.write(Integer.parseInt("EF", 16));
             * stream.write(Integer.parseInt("BB", 16));
             * stream.write(Integer.parseInt("BF", 16));
             */
            for (String key : builtinSection.stringPropertyNames()) {
                writer.write(key + "=" + builtinSection.getProperty(key) + "\r\n");
            }
            for (String section : sectionMap.keySet()) {
                writer.write("\r\n");
                writer.write("[" + section + "]");
                for (String key : sectionMap.get(section).stringPropertyNames()) {
                    writer.write("\r\n" + key + "=" + sectionMap.get(section).getProperty(key));
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

package com.ai.app.aitask.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class ConfigurationFile {

    private final static Logger                  log = Logger.getLogger(ConfigurationFile.class);

    protected HashMap<String, OrderedProperties> sections;
    private transient String                     currentSecion;
    private transient String                     filename;
    private transient OrderedProperties          current;
    private transient OrderedProperties          builtin;

    public ConfigurationFile(String filename) {
        this.sections = new HashMap<String, OrderedProperties>();
        this.filename = filename;
        if ((new File(filename)).exists()) {
            try {
                InputStream is = new BufferedInputStream(new FileInputStream(this.filename));
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                read(reader);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void read(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            log.debug(line);
            parseLine(line);
        }
    }

    protected void parseLine(String line) {
        line = line.trim();
        if (line.matches(".?\\[.*\\]")) {
            currentSecion = line.replaceFirst(".?\\[(.*)\\]", "$1");
            current = new OrderedProperties();
            sections.put(currentSecion, current);
        } else if (line.matches(".*=.*")) {
            if (current == null) {
                current = builtin = new OrderedProperties();
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

    public String getValue(String section, String name) {
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

    public void setValue(String section, String name, String value) {
        if (!sections.containsKey(section)) {
            sections.put(section, new OrderedProperties());
        }
        sections.get(section).setProperty(name, value);
    }

    public void write() throws IOException {

        FileOutputStream fw = new FileOutputStream(this.filename);
        BufferedOutputStream bos = new BufferedOutputStream(fw);
        OutputStreamWriter bw = new OutputStreamWriter(bos, "UTF-8");

        bos.write(Integer.parseInt("EF", 16));
        bos.write(Integer.parseInt("BB", 16));
        bos.write(Integer.parseInt("BF", 16));

        for (String section : sections.keySet()) {
            bw.write("\r\n");
            bw.write("[" + section + "]\r\n");
            for (String key : sections.get(section).stringPropertyNames()) {
                bw.write(key + "=" + sections.get(section).getProperty(key) + "\r\n");
            }
            bw.write("\n");
        }
        bw.close();
        bos.close();
        fw.close();
    }
}

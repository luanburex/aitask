package com.ai.app.aitask.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;


public class ConfigurationFile {

	private final static transient Logger log = Logger.getLogger(ConfigurationFile.class);
	
	protected HashMap<String, OrderedProperties> sections = new HashMap<String, OrderedProperties>();
	private transient String currentSecion;
	private transient OrderedProperties current;
	private transient String filename;

	public ConfigurationFile(String filename) throws IOException {
		this.filename = filename;
		if ((new File(filename)).exists()) {
			
			InputStreamReader sr = new InputStreamReader(
					new BufferedInputStream(new FileInputStream(this.filename)),
					"UTF-8");
			
			BufferedReader reader = new BufferedReader(sr);
			read(reader);
			reader.close();
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
			if (current != null) {
				int i = line.indexOf('=');
				String name = line.substring(0, i);
				String value = line.substring(i + 1);
				current.setProperty(name, value);
			}
		}
	}

	public String[] getSections(){
		ArrayList<String> section_arraylist = new ArrayList<String>();
		for(String key:sections.keySet()){
			section_arraylist.add(key);
		}
		return  section_arraylist.toArray(new String[section_arraylist.size()]);
	}
	
	public String getValue(String section, String name) {
		OrderedProperties p = (OrderedProperties) sections.get(section);

		if (p == null) {
			return null;
		}

		String value = p.getProperty(name);
		return value;
	}
	
	public OrderedProperties getProperties(String section) {
		return sections.get(section);
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

		bos.write((int) Integer.parseInt("EF", 16));
		bos.write((int) Integer.parseInt("BB", 16));
		bos.write((int) Integer.parseInt("BF", 16));

		for (String section : sections.keySet()) {
			bw.write("\r\n");
			bw.write("[" + section + "]\r\n");
			for(String key:sections.get(section).stringPropertyNames()){
				bw.write(key + "=" + sections.get(section).getProperty(key) + "\r\n");
			}
			bw.write("\n");
		}
		bw.close();
		bos.close();
		fw.close();

	}

}

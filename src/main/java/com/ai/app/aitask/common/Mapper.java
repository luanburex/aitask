package com.ai.app.aitask.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Mapper {
    public static Map<String, Object> parseXML(String content) throws Exception {
        try {
            return parseXML(DocumentHelper.parseText(content).getRootElement());
        } catch (DocumentException e) {
            throw new Exception("fail parse xml");
        }
    }
    private static Map<String, Object> parseXML(Element e) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Object a : e.attributes()) {
            Attribute attr = (Attribute) a;
            map.put(attr.getName(), attr.getValue());
        }
        for (Object c : e.elements()) {
            Element child = (Element) c;
            List<Object> list = Caster.cast(map.get(child.getName()), null);
            if (null == list) {
                map.put(child.getName(), list = new LinkedList<Object>());
            }
            list.add(parseXML(child));
        }
        return map;
    }
    public static Map<String, Object> parseJSON(String content) {
        return parseGSONMap(new JsonParser().parse(content).getAsJsonObject());
    }
    public static Map<String, Object> parseGSONMap(JsonObject j) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Entry<String, JsonElement> entry : j.entrySet()) {
            JsonElement e = entry.getValue();
            if (e.isJsonObject()) {
                map.put(entry.getKey(), parseGSONMap(e.getAsJsonObject()));
            } else if (e.isJsonArray()) {
                map.put(entry.getKey(), parseGSONArray(e.getAsJsonArray()));
            } else if (e.isJsonPrimitive()) {
                map.put(entry.getKey(), e.getAsJsonPrimitive().getAsString());
            }
        }
        return map;
    }
    private static List<Object> parseGSONArray(JsonArray j) {
        List<Object> list = new LinkedList<Object>();
        for (Iterator<JsonElement> i = j.getAsJsonArray().iterator(); i.hasNext();) {
            JsonElement c = i.next();
            if (c.isJsonObject()) {
                list.add(parseGSONMap(c.getAsJsonObject()));
            } else if (c.isJsonArray()) {
                list.add(parseGSONArray(c.getAsJsonArray()));
            } else if (c.isJsonPrimitive()) {
                list.add(c.getAsJsonPrimitive().getAsString());
            }
        }
        return list;
    }
    /**
     * dict中的有值key可以匹配替换source中该key对应的子Array/Map
     * dict中""空值key可以匹配其他entry
     */
    public static void transfer(Map<String, Object> source, Map<String, Map<String, String>> dict) {
        Map<String, String> generalDict = dict.get("");
        Set<Entry<String, Object>> set = new HashSet<Entry<String, Object>>(source.entrySet());
        for (Entry<String, Object> entry : set) {
            String key = entry.getKey();
            if (null != generalDict && generalDict.containsKey(key)) {
                source.put(generalDict.get(key), source.remove(key));
            }
            Map<String, String> namedDict = dict.get(key);
            if (entry.getValue() instanceof Map) {
                Map<String, Object> child = Caster.cast(entry.getValue(), null);
                if (null != namedDict) {
                    Set<Entry<String, Object>> cSet;
                    cSet = new HashSet<Entry<String, Object>>(child.entrySet());
                    for (Entry<String, Object> cEntry : cSet) {
                        String cKey = cEntry.getKey();
                        if (namedDict.containsKey(cKey)) {
                            child.put(namedDict.get(cKey), child.remove(cKey));
                        }
                    }
                }
                transfer(child, dict);
            } else if (entry.getValue() instanceof List) {
                List<Object> list = Caster.cast(entry.getValue(), null);
                if (!list.isEmpty() && list.get(0) instanceof Map) {
                    List<Map<String, Object>> mapList = Caster.cast(list, null);
                    for (Map<String, Object> child : mapList) {
                        if (null != namedDict) {
                            Set<Entry<String, Object>> cSet;
                            cSet = new HashSet<Entry<String, Object>>(child.entrySet());
                            for (Entry<String, Object> cEntry : cSet) {
                                String cKey = cEntry.getKey();
                                if (namedDict.containsKey(cKey)) {
                                    child.put(namedDict.get(cKey), child.remove(cKey));
                                }
                            }
                        }
                        transfer(child, dict);
                    }
                }
            }
        }
    }
}

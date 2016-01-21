package com.ai.app.aitask.common;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Lang {

    public static boolean isNumeric(String str) {
        if (str == null || str.length() <= 0) {
            return false;
        }
        return str.matches("^\\d*$");
    }

    public static String formatXML(String retStr) throws Exception {
        String res = null;
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(retStr);
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
        // 格式化XML
        OutputFormat format = new OutputFormat();
        // 设置元素是否有子节点都输出
        format.setExpandEmptyElements(true);
        // 设置不输出XML声明
        format.setSuppressDeclaration(true);

        OutputStream outputStream = new ByteArrayOutputStream();
        XMLWriter writer = new XMLWriter(outputStream, format);
        writer.write(doc);
        writer.close();
        res = outputStream.toString();
        return res;
    }

}

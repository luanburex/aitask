package com.ai.app.aitask.schedule;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.text.html.parser.Entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

import com.ai.app.aitask.deamon.ScheduleDaemon;
import com.ai.app.aitask.net.RequestWorker;
import com.ai.app.aitask.utils.FileReaderUtils;
import com.ai.app.aitask.utils.TestJettyServer;
import com.ai.app.aitask.utils.TriggerStateWaitUnil;

public class CopyOfTaskSyncTester {

    public static void main(String[] args) {
        String xml_file = Thread.currentThread().getContextClassLoader().getResource("").getPath()
                .toString()
                + "/com/ai/app/aitask/schedule/task_xml_001.xml";
        xml_file = Thread.currentThread().getContextClassLoader().getResource("").getPath()
                .toString()
                + "/com/ai/app/aitask/task/bat/Copy of bat_script_task001.xml";
        String xml_str = FileReaderUtils.readFileToString(xml_file);
        // RequestWorker worker = new RequestWorker("http://10.1.55.57:8888/arrange");
        RequestWorker worker = new RequestWorker(
                "http://10.1.55.14:8002/aiga/service?action=receivePost&isconvert=true");
        BasicNameValuePair p = new BasicNameValuePair("xml", "test");
        ArrayList<NameValuePair> l = new ArrayList<NameValuePair>();
        l.add(p);
        try {
            UrlEncodedFormEntity e = new UrlEncodedFormEntity(l, "UTF-8");
            worker.Post(EntityUtils.toString(e), ContentType.APPLICATION_JSON);
            System.out.println(worker.getResponseMessage());
            System.out.println(worker.getResponseContent());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}

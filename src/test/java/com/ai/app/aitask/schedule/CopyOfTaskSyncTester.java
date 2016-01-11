package com.ai.app.aitask.schedule;

import com.ai.app.aitask.common.Config;

public class CopyOfTaskSyncTester {

    public static void main(String[] args) {
        Config cf = Config.instance("2.properties");
        
        
        cf.getProperties(null).put("test", "tttt");
        cf.write();
//        String xml_file = Thread.currentThread().getContextClassLoader().getResource("").getPath()
//                .toString()
//                + "/com/ai/app/aitask/schedule/task_xml_001.xml";
//        xml_file = Thread.currentThread().getContextClassLoader().getResource("").getPath()
//                .toString()
//                + "/com/ai/app/aitask/task/bat/Copy of bat_script_task001.xml";
//        String xml_str = FileReaderUtils.readFileToString(xml_file);
//        // RequestWorker worker = new RequestWorker("http://10.1.55.57:8888/arrange");
//        RequestWorker worker = new RequestWorker(
//                "http://10.1.55.14:8002/aiga/service?action=receivePost&isconvert=true");
//        BasicNameValuePair p = new BasicNameValuePair("xml", "test");
//        ArrayList<NameValuePair> l = new ArrayList<NameValuePair>();
//        l.add(p);
//        l.add(new BasicNameValuePair("ddd", "dddd"));
//        try {
//            UrlEncodedFormEntity e = new UrlEncodedFormEntity(l, "UTF-8");
//            System.out.println(EntityUtils.toString(e));
////            worker.Post(EntityUtils.toString(e), ContentType.APPLICATION_JSON);
////            System.out.println(worker.getResponseMessage());
////            System.out.println(worker.getResponseContent());
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ParseException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
    }
}

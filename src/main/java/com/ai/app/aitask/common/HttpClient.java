package com.ai.app.aitask.common;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClient {

	transient final public static Log log = LogFactory.getLog(HttpClient.class);
	
	/** 
     * 发送 post请求访问本地应用并根据传递参数不同返回不同结果 
	 * @throws IOException 
     */  
    public static String post(String url, String post_body, String content_type) throws IOException {  
    	
    	String response_body = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Content-Type", content_type);
        //List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
        //formparams.add(new BasicNameValuePair("type", "house"));  
        //UrlEncodedFormEntity uefEntity;  
        try {  
            //uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
            //httppost.setEntity(uefEntity);
        	StringEntity reqEntity = new StringEntity(post_body, "UTF-8");
        	httppost.setEntity(reqEntity);
            log.debug("executing request " + httppost.getURI());  
            CloseableHttpResponse response = httpclient.execute(httppost);  
            try {  
                HttpEntity entity = response.getEntity();  
                if (entity != null) {  
                	response_body = EntityUtils.toString(entity, "UTF-8");
                    log.debug("--------------------------------------");  
                    log.debug("Response content: " + response);  
                    log.debug("--------------------------------------");  
                }  
            } finally {  
                response.close();  
            }  
        } finally {  
            try {  
                httpclient.close();
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
		return response_body;  
    }  
public static String post(String url, HttpEntity entity, String content_type) throws IOException {  
        
        String response_body = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Content-Type", content_type);
        //List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
        //formparams.add(new BasicNameValuePair("type", "house"));  
        //UrlEncodedFormEntity uefEntity;  
        try {  
//            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
            //httppost.setEntity(uefEntity);
            httppost.setEntity(entity);
            log.debug("executing request " + httppost.getURI());  
            CloseableHttpResponse response = httpclient.execute(httppost);  
            try {  
                HttpEntity rentity = response.getEntity();  
                if (rentity != null) {  
                    response_body = EntityUtils.toString(rentity, "UTF-8");
                    log.debug("--------------------------------------");  
                    log.debug("Response content: " + response);  
                    log.debug("--------------------------------------");  
                }  
            } finally {  
                response.close();  
            }  
        } finally {  
            try {  
                httpclient.close();
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
        return response_body;  
    }  
}

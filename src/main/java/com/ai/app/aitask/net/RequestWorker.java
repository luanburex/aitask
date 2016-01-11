package com.ai.app.aitask.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.ContentType;

import com.ai.app.aitask.common.Constants;

public class RequestWorker implements Constants {
    private final Log log = LogFactory.getLog(getClass());
    private String    requestURL;
    private String    requestQuery;
    private Integer   responseCode;
    private String    responseContent;
    private String    responseMessage;
    public RequestWorker(String url, String query) {
        this.requestURL = url;
        this.requestQuery = query;
    }
    public RequestWorker Get() throws ConnectException {
        return Post(null, ContentType.DEFAULT_TEXT);
    }
    public RequestWorker Post(String content, ContentType type) throws ConnectException {
        StringBuffer buffer = new StringBuffer(requestURL);
        if (null != requestQuery && !requestQuery.isEmpty()) {
            try {
                buffer.append(URLEncoder.encode(requestQuery, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        log.info("url:" + buffer.toString());
        HttpURLConnection conn = null;
        try {
            URL url = new URL(buffer.toString());
            conn = (HttpURLConnection) url.openConnection();
            if (content != null) {
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", type.getMimeType());
                conn.setUseCaches(false);
                conn.connect();

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                BufferedWriter bufferWriter = new BufferedWriter(writer);
                bufferWriter.write(content);
                bufferWriter.flush();
                bufferWriter.close();
            } else {
                conn.connect();
            }
            this.responseCode = conn.getResponseCode();
            this.responseMessage = conn.getResponseMessage();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            ConnectException ce = new ConnectException("failed connecting");
            ce.initCause(e);
            throw ce;
        }
        StringBuffer responseContent = new StringBuffer();
        try {
            if (responseCode == 200) {
                if (conn.getContentLength() > -1) {
                    InputStreamReader reader = new InputStreamReader(conn.getInputStream(), "GBK"); // GBK for some special...
                    BufferedReader bufferReader = new BufferedReader(reader);
                    for (String temp = bufferReader.readLine(); temp != null;) {
                        responseContent.append(temp).append(LINE_SEPARATOR);
                        temp = bufferReader.readLine();
                    }
                }
            } else if (responseCode == 400) {
                InputStreamReader reader = new InputStreamReader(conn.getErrorStream(), "UTF-8");
                BufferedReader bufferReader = new BufferedReader(reader);
                for (String temp = bufferReader.readLine(); temp != null;) {
                    responseContent.append(temp).append(LINE_SEPARATOR);
                    temp = bufferReader.readLine();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.responseContent = responseContent.toString();
        return this;
    }
    public String getResponseContent() {
        return responseContent;
    }
    public String getResponseMessage() {
        return responseMessage;
    }
    public Integer getResponseCode() {
        return responseCode;
    }
    public static String formEntity(Map<String, String> pairmap) {
        StringBuffer buffer = new StringBuffer();
        for (Entry<String, String> pair : pairmap.entrySet()) {
            buffer.append(pair.getKey()).append("=").append(pair.getValue()).append("&");
        }
        return buffer.substring(0, buffer.length() - 1);
    }
    public static String formEntityEX(Map<String, String> pairmap) {
        return "?".concat(formEntity(pairmap));
    }
}
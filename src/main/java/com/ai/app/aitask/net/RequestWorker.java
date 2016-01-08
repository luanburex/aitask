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

import org.apache.http.entity.ContentType;

import com.ai.app.aitask.common.Constants;

public class RequestWorker implements Constants {
    private StringBuffer queryString   = new StringBuffer();
    private StringBuffer requestString = new StringBuffer();
    private Integer      responseCode;
    private String       responseContent;
    private String       responseMesssage;
    public RequestWorker(String url) {
        requestString.append(url);
    }
    public RequestWorker setQueryParameter(String key, String value) {
        queryString.append(key).append("=");
        try {
            value = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        queryString.append(value).append("&");
        return this;
    }
    public RequestWorker Get() {
        return Post(null, ContentType.DEFAULT_TEXT);
    }
    public RequestWorker Post(String content, ContentType type) {
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
            requestString.append("?").append(queryString);
            System.out.println(requestString);
        }
        try {
            URL url = new URL(requestString.toString().replace(' ', '+'));
            HttpURLConnection urlConn = HttpURLConnection.class.cast(url.openConnection());
            if (content != null) {
                urlConn.setDoOutput(true);
                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Content-Type", type.getMimeType());
                urlConn.setUseCaches(false);
                urlConn.connect();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        urlConn.getOutputStream(), "UTF-8"));
                writer.write(content);
                writer.flush();
                writer.close();
            } else {
                urlConn.connect();
            }

            this.responseCode = urlConn.getResponseCode();
            this.responseMesssage = urlConn.getResponseMessage();
            StringBuffer responseContent = new StringBuffer();
            if (urlConn.getResponseCode() == 200) {
                if (urlConn.getContentLength() > -1) {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            urlConn.getInputStream(), "GBK"));
                    // GBK for some special...
                    for (String temp = reader.readLine(); temp != null;) {
                        responseContent.append(temp).append(LINE_SEPARATOR);
                        temp = reader.readLine();
                    }
                }
            }
            // TODO for debuggin?
            if (urlConn.getResponseCode() == 400) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        urlConn.getErrorStream(), "UTF-8"));
                for (String temp = reader.readLine(); temp != null;) {
                    responseContent.append(temp).append(LINE_SEPARATOR);
                    temp = reader.readLine();
                }
            }
            this.responseContent = responseContent.toString();
        } catch (ConnectException e) {
            System.err.println(e.toString());
        } catch (MalformedURLException e) {
            System.err.println(e.toString());
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return this;
    }
    public String getResponseContent() {
        return responseContent;
    }
    public String getResponseMessage() {
        return responseMesssage;
    }
    public Integer getResponseCode() {
        return responseCode;
    }
}
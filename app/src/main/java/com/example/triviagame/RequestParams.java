package com.example.triviagame;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by batesjernigan on 9/25/15.
 */
public class RequestParams {
    String method, baseUrl;
    HashMap<String, String> params = new HashMap<String, String>();

    public RequestParams(String method, String baseUrl) {
        this.baseUrl = baseUrl;
        this.method = method;
    }

    @Override
    public String toString() {
        return "RequestParams{" +
            "baseUrl='" + baseUrl + '\'' +
            ", method='" + method + '\'' +
            ", params=" + params +
            '}';
    }

    public void addParam(String key, String value) {
        params.put(key, value);
    }

    public void removeParam(String pid) {
        params.remove(pid);
    }

    public String getEncodedParams() {
        // loop over the key/value pairs of the params
        // append to a string builder key=value, join on &
        // param1=value1%20value1&param2=value2
        StringBuilder stringBuilder = new StringBuilder();

        for(String key: params.keySet()) {
            try {
                String value = URLEncoder.encode(params.get(key), "UTF-8");
                if (stringBuilder.length() > 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(key + "=" + value);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    public String getEncodedUrl() {
        return this.baseUrl + "?" + getEncodedParams();
    }

    public HttpURLConnection setupConnection() throws IOException {
        if(method.equals("GET")) {
            URL url = new URL(getEncodedUrl());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            Log.d("demo", "con: " + con);
            return con;
        } else { //POST
            URL url = new URL(this.baseUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(getEncodedParams());
            writer.flush();
            return con;
        }
    }
}

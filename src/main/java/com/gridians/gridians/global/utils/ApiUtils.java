package com.gridians.gridians.global.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public class ApiUtils {

    private static HttpURLConnection connection;
    public static String request(String url, String method) throws Exception{
        connection = setHttpUrlConnection(url, method);

        return getResponse();
    }

    public static String requestWithHeader(String url, String method, Map<String, String> headers) throws Exception{
        connection = setHttpUrlConnection(url, method);
        setHeader(headers);

        return getResponse();
    }
    public static String requestWithHeaderAndParam(String url, String method, Map<String, String> headers, Map<String, String> parameters) throws Exception{
        connection = setHttpUrlConnection(url, method);
        setHeader(headers);
        setParameters(parameters);

        return getResponse();
    }
    public static String requestWithHeaderAndBody(String url, String method, Map<String, String> headers, Map<String, String> bodies) throws Exception{
        connection = setHttpUrlConnection(url, method);
        setHeader(headers);
        setBody(bodies);

        return getResponse();
    }
    public static String requestWithHeaderAndParamAndBody(
            String url, String method, Map<String, String> headers,
            Map<String, String> parameters, Map<String, String> bodies
    ) throws Exception {
        connection = setHttpUrlConnection(url, method);
        setHeader(headers);
        setParameters(parameters);
        setBody(bodies);

        return getResponse();
    }
    public static String requestWithParamAndNoHeader(String url, String method, Map<String, String> parameters) throws Exception {
        connection = setHttpUrlConnection(url, method);
        setParameters(parameters);

        return getResponse();
    }
    public static String requestWithBodyAndNoHeader(String url, String method, Map<String, String> bodies) throws Exception{
        connection = setHttpUrlConnection(url, method);
        setBody(bodies);
        return getResponse();
    }
    public static String requestWithParametersAndBodyAndNoHeader(
            String url, String method, Map<String, String> parameters, Map<String, String> bodies
    ) throws Exception{
        connection = setHttpUrlConnection(url, method);
        setParameters(parameters);
        setBody(bodies);

        return getResponse();
    }

    private static HttpURLConnection setHttpUrlConnection(String requestUrl, String method) throws Exception {
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod(method);

        return conn;
    }

    private static void setHeader(Map<String, String> headers) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
    }

    private static void setParameters(Map<String, String> parameters) throws Exception {
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> parameter : parameters.entrySet()){
            if(first) {
                first = false;
            }
            else {
                sb.append("&");
            }
            sb.append(parameter.getKey());
            sb.append("=");
            sb.append(parameter.getValue());
        }

        writer.write(sb.toString());
        writer.flush();
        writer.close();
        connection.getOutputStream().close();
    }

    private static void setBody(Map<String, String> bodies) {

    }

    private static String getResponse() throws Exception {
        if (connection.getResponseCode() / 100 != 2) {
            InputStream errorStream = connection.getErrorStream();
            Scanner s = new Scanner(errorStream).useDelimiter("\\A");
            String error = s.hasNext() ? s.next() : "";

            System.out.println(error);
            throw new RuntimeException("Bad Request Exception");
        }

        InputStream responseStream = connection.getInputStream();

        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";

        return response;
    }

}

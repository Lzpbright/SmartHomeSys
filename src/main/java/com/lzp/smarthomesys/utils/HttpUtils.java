package com.lzp.smarthomesys.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    /**
     * use: 用来对关闭流的封装
     * @param response response
     * @param httpRequestBase httpRequestBase
     * @param httpClient httpClient
     */
    public static void close(CloseableHttpResponse response, HttpRequestBase httpRequestBase, CloseableHttpClient httpClient){
        try {
            if (response != null) {
                response.close();
            }

            if (httpRequestBase!= null){
                httpRequestBase.releaseConnection();
            }

            if (httpClient != null){
                httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * use: 封装GET方法，
     * @param url 定位符
     * @param headers 请求头
     * @param params 参数
     * @return String
     */
    public static String sendGet(String url, Map<String, String> headers, Map<String,String> params) {
        CloseableHttpClient httpClient = null;
        HttpGet httpGet = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            httpClient = HttpClientBuilder.create().build();
            httpGet = new HttpGet(url);
            // 设置请求头
            if (null != headers && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置参数
            if (null != params && params.size() > 0){
                List<BasicNameValuePair> pairList = new ArrayList<>();
                params.forEach((x,y) -> pairList.add(new BasicNameValuePair(x,y)));
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairList,"utf-8");
                // 将参数转成page=1&limit=5格式
                String param = EntityUtils.toString(urlEncodedFormEntity, "utf-8");
                httpGet = new HttpGet(url+"?"+param);
            }
            response = httpClient.execute(httpGet);
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(response,httpGet,httpClient);
        }
        return result;
    }


    /**
     * use: 对于Post的键值对封装
     * @param url 定位符
     * @param headers 请求头
     * @param params 参数
     * @return result
     */
    public static String sendPost(String url, Map<String, String> headers, Map<String,String> params){
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            httpClient = HttpClientBuilder.create().build();
            httpPost = new HttpPost(url);
            // 设置请求头
            if (null != headers && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置参数
            if (null != params && params.size() > 0){
                List<BasicNameValuePair> pairList = new ArrayList<>();
                params.forEach((x,y) -> pairList.add(new BasicNameValuePair(x,y)));
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairList,"utf-8");
                httpPost.setEntity(urlEncodedFormEntity);
            }
            response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(response, httpPost, httpClient);
        }
        return result;
    }


    /**
     * use: 对于Json数据Post的封装
     * @param url 定位符
     * @param headers 请求头
     * @param params 参数
     * @return params
     */
    public static String sendPostJson(String url, Map<String, String> headers, Map<String,String> params){
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            httpClient = HttpClientBuilder.create().build();
            httpPost = new HttpPost(url);
            // 设置请求头
            if (null != headers && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置参数
            if (null != params && params.size() > 0){
                String paramJson = JSONObject.toJSONString(params);
                StringEntity stringEntity = new StringEntity(paramJson,"utf-8");
                stringEntity.setContentType("application/json;charset=utf-8");
                httpPost.setEntity(stringEntity);
            }
            response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(response,httpPost,httpClient);
        }
        return result;
    }

    public static String sendPostAFile(String url, Map<String, String> params,Map<String, String> headers, File body){
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            httpClient = HttpClientBuilder.create().build();
            // 设置url参数
            if (null != params && params.size() > 0) {
                url += "?";
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    url += entry.getKey() + "=" + entry.getValue() + "&";
                }
            }
            httpPost = new HttpPost(url);
            // 设置请求头
            if (null != headers && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置请求体
            FileEntity fileEntity = new FileEntity(body);
            fileEntity.setContentType("application/octet-stream");
            httpPost.setEntity(fileEntity);
            response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(response,httpPost,httpClient);
        }
        return result;
    }


}

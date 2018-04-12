package com.yvan.util;

import com.yvan.exception.BaseException;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.List;

/**
 * Function：请求工具类
 * Created by YangWang on 2018-03-26 23:49.
 */
public class RestUtil {

    /**
     * 主处理方法
     *
     * @param type      请求方法
     * @param restUrl   请求地址
     * @param params    请求参数
     * @return          结果
     */
    public static String process (String type, String restUrl, List<NameValuePair> params) throws BaseException {
        String result = null;
        if ("post".equalsIgnoreCase(type)) {
            result = doPost(restUrl, params);
        } else if ("get".equalsIgnoreCase(type)) {
            result = doGet(restUrl, params);
        }
        return result;
    }

    /**
     * 针对于post类型接口的处理方案
     *
     * @return
     */
    private static String doPost (String restUrl, List<NameValuePair> params) throws BaseException {
        HttpPost httpPost = new HttpPost(restUrl);
        String result = null;
        try {
            // 1.参数封装到请求体当中
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            // 2.准备客户端（HttpClient）
            CloseableHttpClient httpClient = HttpClients.createDefault();
            // 3.提交请求
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            // 4.解析接口返回数据
            result = EntityUtils.toString(httpResponse.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    /**
     * 针对于get类型接口的处理方案
     *
     * @param restUrl
     * @param params
     * @return
     */
    private static String doGet(String restUrl, List<NameValuePair> params) throws BaseException {
        String result = null;
        StringBuffer sb = new StringBuffer(restUrl);
        for (int i = 0; i < params.size(); i++) {
            NameValuePair nameValuePair = params.get(i);
            if (i == 0) {
                sb.append("?").append(nameValuePair.getName()).append("=").append(nameValuePair.getValue());
            } else {
                sb.append("&").append(nameValuePair.getName()).append("=").append(nameValuePair.getValue());
            }
        }
        // 准备HttpGet对象,将参数拼接在url
        HttpGet httpGet = new HttpGet(sb.toString());
        // 准备HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            result = EntityUtils.toString(httpResponse.getEntity());
            System.out.println("result=" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 获取接口返回数据
        return result;
    }


}

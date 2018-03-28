package com.yvan;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * Function：http post test
 * Created by YangWang on 2018-03-22 22:56.
 */
public class httpPost {

//    public static void main(String[] args) throws Exception {
//        String restUrl = "http://localhost:8081/scmservice/main/login.do";
//        // 1.决定接口提交方式post
//        HttpPost httpPost = new HttpPost(restUrl);
//        // 2.以post方式，准备参数,account=belle&password=belle
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        BasicNameValuePair basicNameValuePair1 = new BasicNameValuePair("account","belle");
//        BasicNameValuePair basicNameValuePair2 = new BasicNameValuePair("password","belle");
//        params.add(basicNameValuePair1);
//        params.add(basicNameValuePair2);
//        // 参数封装到请求体当中
//        httpPost.setEntity(new UrlEncodedFormEntity(params));
//        // 3.准备客户端（HttpClient）
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        // 4.提交请求
//        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
//        // 5.解析接口返回数据
//        String result = EntityUtils.toString(httpResponse.getEntity());
//        System.out.println(result);
//    }

}

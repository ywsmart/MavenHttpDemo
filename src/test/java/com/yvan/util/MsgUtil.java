package com.yvan.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Function：信息工具类
 * Created by YangWang on 2018-04-10 0:14.
 */
public class MsgUtil {

    /**
     * 钉钉机器人网钩地址
     */
//    我的测试机器人
    private static final String WEBHOOK_TOKEN = "https://oapi.dingtalk.com/robot/send?access_token=e3e0d361113b62b57c49bf1262a9d93f3c72510fa1c6b988d51ac8952b552306";
//    测试中心机器人
//    private static final String WEBHOOK_TOKEN = "https://oapi.dingtalk.com/robot/send?access_token=48d320c26e84030c01c7610d6b2334f25dcd2800ab1643f51e3249b15cdb11d7";

    /**
     * 钉钉群机器人信息提示
     *
     * @param msg 提示信息
     * @throws IOException IO异常
     */
    public static void dingDingMsg(String msg) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(WEBHOOK_TOKEN);
        httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
//        String msg = "我就是我，是不一样的烟火！\nXX轮测试已完成，快来查看吧！";
        String textMsg = "{\"msgtype\":\"text\",\"text\":{\"content\":\"" + msg + "\"}}";
        StringEntity stringEntity = new StringEntity(textMsg, "utf-8");
        httpPost.setEntity(stringEntity);
        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(result);
        }
    }

}

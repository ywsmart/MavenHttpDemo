package com.yvan;

import com.alibaba.fastjson.JSONObject;
import com.yvan.config.RestConfig;
import com.yvan.util.ExcelUtil;
import com.yvan.util.RestUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Function：请求的中央处理类
 * Created by YangWang on 2018-03-25 1:13.
 */
public class RestUniformProcessor {

    @Test(dataProvider = "datas")
    public void test(String caseId, String apiId, String requestData) throws Exception {
        // 获取接口地址
        String restUrl = RestConfig.getRestUrlByApiId(apiId);
        // 获取接口提交方式
        String type = RestConfig.getRestTypeByApiId(apiId);
        // 准备参数
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Map<String, String> map = (Map<String, String>) JSONObject.parse(requestData);
        Set<String> keys = map.keySet();
        // 循环键，匹配参数和键值
        for (String key :
                keys) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(key, map.get(key));
            params.add(basicNameValuePair);
        }
        // 处理接口调用，获取结果
        String result = RestUtil.process(type, restUrl, params);
        // 批量写入Excel
        ExcelUtil.addTestResult(caseId, 5, result);
    }

    /**
     * 批量写入Excel
     */
    @AfterSuite
    public void batchWriteBackData() {
        ExcelUtil.batchWrite("target/test-classes/rest_infos.xlsx", 2);
    }

    @DataProvider
    public Object[][] datas() {
        Object[][] datas = ExcelUtil.read("src/test/resources/rest_infos.xlsx", 2, 2, 4, 1, 3);
        return datas;
    }

}

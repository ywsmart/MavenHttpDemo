package com.yvan.config;

import com.yvan.pojo.Rest;
import com.yvan.util.ExcelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Function：请求参数类
 * Created by YangWang on 2018-03-25 1:53.
 */
public class RestConfig {
    public static List<Rest> restInfos;

    static {
        restInfos = new ArrayList<Rest>();

        Object[][] datas = ExcelUtil.read("src/main/resources/rest_infos.xlsx", 1, 2, 3, 1, 4);
        for (Object[] objects :
                datas) {
            Rest rest = new Rest();
            for (int i = 0; i < objects.length; i++) {
                String value = objects[i].toString();
                if (i == 0) {
                    rest.setApiId(value);
                }
                if (i == 1) {
                    rest.setApiName(value);
                }
                if (i == 2) {
                    rest.setType(value);
                }
                if (i == 3) {
                    rest.setUrl(value);
                }
            }
            restInfos.add(rest);
        }
    }

    /**
     * 根据接口端号返回接口地址
     *
     * @param apiId
     * @return
     */
    public static String getRestUrlByApiId(String apiId) {
        for (Rest rest :
                restInfos) {
            if (apiId.equals(rest.getApiId())) {
                return rest.getUrl();
            }
        }
        return "";
    }

    /**
     * 根据接口端号返回请求方式
     *
     * @param apiId
     * @return
     */
    public static String getRestTypeByApiId(String apiId) {
        for (Rest rest :
                restInfos) {
            if (apiId.equals(rest.getApiId())) {
                return rest.getType();
            }
        }
        return "";
    }

}

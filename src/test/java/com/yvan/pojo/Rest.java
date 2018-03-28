package com.yvan.pojo;

/**
 * Function：
 * Created by YangWang on 2018-03-25 1:56.
 */
public class Rest {
    /**
     * api编号
     */
    private String url;
    /**
     * api名称
     */
    private String apiName;
    /**
     * 接口请求方式
     */
    private String type;
    /**
     * 接口地址
     */
    private String apiId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    @Override
    public String toString() {
        return "apiId:" + this.apiId + ",apiName:" + this.apiName + ",tupe:" + this.type + ",url:" + this.url;
    }
}

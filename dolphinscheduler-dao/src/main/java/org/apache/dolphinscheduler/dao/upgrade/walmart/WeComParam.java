package org.apache.dolphinscheduler.dao.upgrade.walmart;

public class WeComParam {
    private String appName;

    private String addressList;

    private String businessSubject;

    private String format;

    private String accessToken;

    private String type;

    private String url;

    public WeComParam() {
    }

    public WeComParam(String addressList) {
        this.addressList = addressList;
        this.appName = "cndlplatform";
        this.businessSubject = "metadata platform alert";
        this.format = "json";
        this.accessToken = "72CB89FFB365DACAA60A768738FbC8B8";
        this.type = "wework";
        this.url = "https://aloha.cn.wal-mart.com/aloha-notice-service/notice/send";
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAddressList() {
        return addressList;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

    public String getBusinessSubject() {
        return businessSubject;
    }

    public void setBusinessSubject(String businessSubject) {
        this.businessSubject = businessSubject;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

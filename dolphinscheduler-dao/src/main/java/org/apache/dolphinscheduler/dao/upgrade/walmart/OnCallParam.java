package org.apache.dolphinscheduler.dao.upgrade.walmart;

public class OnCallParam {

    private String url;

    private String serverity;

    public OnCallParam() {
        this.url = "http://10.233.49.59:8081/api/zabbix/insert";
        this.serverity = "High";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getServerity() {
        return serverity;
    }

    public void setServerity(String serverity) {
        this.serverity = serverity;
    }
}

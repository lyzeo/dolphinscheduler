package org.apache.dolphinscheduler.dao.upgrade.walmart;

public class EmailParam {
    private String receivers;

    private String receiverCcs;

    private String serverHost;

    private String serverPort;

    private String sender;

    private String enableSmtpAuth;

    private String user;

    private String passwd;

    private String starttlsEnable;

    private String sslEnable;

    private String smtpSslTrust;

    private String showType;

    public EmailParam() {
    }

    public EmailParam(String receivers, String receiverCcs) {
        this.receivers = receivers;
        this.receiverCcs = receiverCcs;
        this.serverHost = "exchange.cn.wal-mart.com";
        this.serverPort = "25";
        this.sender = "cnpipeline@wal-mart.com";
        this.enableSmtpAuth = "false";
        this.user = null;
        this.passwd = null;
        this.starttlsEnable = "false";
        this.sslEnable = "false";
        this.smtpSslTrust = "*";
        this.showType = "table";
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    public String getReceiverCcs() {
        return receiverCcs;
    }

    public void setReceiverCcs(String receiverCcs) {
        this.receiverCcs = receiverCcs;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getEnableSmtpAuth() {
        return enableSmtpAuth;
    }

    public void setEnableSmtpAuth(String enableSmtpAuth) {
        this.enableSmtpAuth = enableSmtpAuth;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getStarttlsEnable() {
        return starttlsEnable;
    }

    public void setStarttlsEnable(String starttlsEnable) {
        this.starttlsEnable = starttlsEnable;
    }

    public String getSslEnable() {
        return sslEnable;
    }

    public void setSslEnable(String sslEnable) {
        this.sslEnable = sslEnable;
    }

    public String getSmtpSslTrust() {
        return smtpSslTrust;
    }

    public void setSmtpSslTrust(String smtpSslTrust) {
        this.smtpSslTrust = smtpSslTrust;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }
}

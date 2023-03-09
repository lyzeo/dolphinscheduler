package org.apache.dolphinscheduler.plugin.alert.walmart.oncall;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.dolphinscheduler.alert.api.AlertResult;
import org.apache.dolphinscheduler.spi.utils.JSONUtils;
import org.apache.dolphinscheduler.spi.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class OnCallSender {

    private static final Logger log = LoggerFactory.getLogger(OnCallSender.class);

    private final String url;

    private final String serverity;

    public OnCallSender(Map<String, String> config) {
        url = config.get(OnCallAlertParamsConstants.ONCALL_PARAMS_URL);
        serverity = config.get(OnCallAlertParamsConstants.ONCALL_PARAMS_SERVERITY);
    }

    public AlertResult send(String title, String content) {
        ObjectNode node = JSONUtils.parseObject(content);
        node.put("alertType", title);
        String host = node.get(OnCallAlertConstants.HOST).asText();

        String status = title.contains("success") ? StatusType.RESOLVED.getDesc() : StatusType.SIREN.getDesc();

        SimpleDateFormat dateFormat = new SimpleDateFormat(OnCallAlertConstants.DATE_FORMAT);
        SimpleDateFormat timeFormat = new SimpleDateFormat(OnCallAlertConstants.TIME_FORMAT);

        Map<String, String> params = new HashMap<>();
        params.put(OnCallAlertConstants.API_PARAM_HOSTNAME, OnCallAlertConstants.HOSTNAME_PREFIX + host);
        params.put(OnCallAlertConstants.API_PARAM_EVENTNAME, JSONUtils.toJsonString(node));
        params.put(OnCallAlertConstants.API_PARAM_SERVERITY, serverity);
        params.put(OnCallAlertConstants.API_PARAM_STATUS, status);
        params.put(OnCallAlertConstants.API_PARAM_EVENTDATE, dateFormat.format(new Date()));
        params.put(OnCallAlertConstants.API_PARAM_EVENTTIME, timeFormat.format(new Date()));

        try {
            return checkOncallSendMsg(post(url, params));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            AlertResult result = new AlertResult();
            result.setStatus("false");
            result.setMessage("oncall send fail," + e.getMessage());
            return result;
        }
    }

    public static String post(String url, Map<String, String> params) throws URISyntaxException {
        String paramsJson = JSONUtils.toJsonString(params);
        HttpPost post = setHttpPost(url, paramsJson);

        try (CloseableHttpClient client = setHttpClient(url);
             CloseableHttpResponse response = client.execute(post)){
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } catch (IOException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static HttpPost setHttpPost(String url, String param) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);
        StringEntity stringEntity = new StringEntity(param, StandardCharsets.UTF_8);

        HttpPost post = new HttpPost(uriBuilder.build());
        post.setEntity(stringEntity);
        post.setConfig(setRequestConfig());
        post.setHeader(OnCallAlertConstants.HTTP_HEADER_ACCEPT, OnCallAlertConstants.APPLICATION_FORMAT);
        post.setHeader(OnCallAlertConstants.HTTP_HEADER_CONTENT_TYPE, OnCallAlertConstants.APPLICATION_FORMAT);
        return post;
    }

    public static RequestConfig setRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(OnCallAlertConstants.TIMEOUT)
                .setConnectionRequestTimeout(OnCallAlertConstants.TIMEOUT)
                .build();
    }

    public static CloseableHttpClient setHttpClient(String url) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        if (url.contains("https")) {
            return setHttpsClient();
        }
        return HttpClients.createDefault();
    }

    public static CloseableHttpClient setHttpsClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();
        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
    }

    public static AlertResult checkOncallSendMsg(String result) {
        AlertResult alertResult = new AlertResult();

        if (StringUtils.isBlank(result)) {
            alertResult.setStatus("false");
            alertResult.setMessage("oncall send fail!");
            log.error("oncall send fail, result is null!");
            return alertResult;
        }

        OnCallSendMsgResponse response = JSONUtils.parseObject(result, OnCallSendMsgResponse.class);
        if (Objects.isNull(response)) {
            alertResult.setStatus("false");
            alertResult.setMessage("oncall send fail");
            log.error("oncall send fail, response is null");
            return alertResult;
        }

        if (response.errorCode == 0) {
            alertResult.setStatus("true");
            alertResult.setMessage("oncall send success!");
            return alertResult;
        }

        alertResult.setStatus("false");
        alertResult.setMessage(response.getErrorMessage());
        return alertResult;
    }

    static final class OnCallSendMsgResponse {
        private Integer errorCode;

        private String errorMessage;

        public OnCallSendMsgResponse() {
        }

        public Integer getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(Integer errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof OnCallSendMsgResponse)) {
                return false;
            }

            final OnCallSendMsgResponse other = (OnCallSendMsgResponse) o;
            final Object this$errorCode = this.getErrorCode();
            final Object other$errorCode = other.getErrorCode();
            if (this$errorCode == null ? other$errorCode != null : !Objects.equals(this$errorCode, other$errorCode)) {
                return false;
            }

            final Object this$errorMessage = this.getErrorMessage();
            final Object other$errorMessage = other.getErrorMessage();
            if (this$errorMessage != null ? other$errorMessage != null : !this$errorMessage.equals(other$errorMessage)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $errorCode = this.getErrorCode();
            result = result * PRIME + ($errorCode == null ? 43 : $errorCode.hashCode());

            final Object $errorMessage = this.getErrorMessage();
            result = result * PRIME + ($errorMessage == null ? 43 : $errorMessage.hashCode());

            return result;
        }

        @Override
        public String toString() {
            return "OnCallSender.OnCallSendMsgResponse{" +
                    "errorCode=" + errorCode +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }
}

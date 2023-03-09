package org.apache.dolphinscheduler.plugin.alert.walmart.wecom;

import org.apache.dolphinscheduler.spi.utils.JSONUtils;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

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
import java.util.Map;

public class HttpUtils {

    private HttpUtils() {
        throw new UnsupportedOperationException("util class can not support instantiate!");
    }

    public static void doPost(String url,
                              Map<String, Object> params,
                              String accessToken,
                              String appName) throws URISyntaxException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String paramJson = JSONUtils.toJsonString(params);
        HttpPost post = setHttpPost(url, paramJson, accessToken, appName);
        sendRequest(setHttpClient(url), post);
    }

    public static HttpPost setHttpPost(String url,
                                       String paramJson,
                                       String accessToken,
                                       String appName) throws URISyntaxException {
        HttpPost post = new HttpPost(new URIBuilder(url).build());
        StringEntity entity = new StringEntity(paramJson, StandardCharsets.UTF_8);
        post.setEntity(entity);
        post.setConfig(setRequestConfig());
        post.setHeader(WeComAlertConstants.HTTP_HEADER_ACCEPT, WeComAlertConstants.APPLICATION_JSON);
        post.setHeader(WeComAlertConstants.HTTP_HEADER_CONTENT_TYPE, WeComAlertConstants.APPLICATION_JSON);
        post.setHeader(WeComAlertConstants.HTTP_HEADER_ACCESS_TOKEN, accessToken);
        post.setHeader(WeComAlertConstants.HTTP_HEADER_ALOHA_APP_NANE, appName);
        return post;
    }

    public static void sendRequest(CloseableHttpClient client, HttpRequestBase method) {
        try (CloseableHttpResponse response = client.execute(method)){
            StatusLine statusLine = response.getStatusLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static RequestConfig setRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(WeComAlertConstants.TIMEOUT)
                .setConnectionRequestTimeout(WeComAlertConstants.TIMEOUT)
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


}

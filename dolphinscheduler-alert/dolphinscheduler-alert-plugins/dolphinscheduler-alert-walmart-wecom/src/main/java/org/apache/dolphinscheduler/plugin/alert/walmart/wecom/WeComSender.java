package org.apache.dolphinscheduler.plugin.alert.walmart.wecom;

import org.apache.dolphinscheduler.alert.api.AlertResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class WeComSender {

    private static final Logger log = LoggerFactory.getLogger(WeComSender.class);

    private final String appName;

    private final String format;

    private final String url;

    private final String accessToken;

    private final List<String> addressList;

    private final String businessSubject;

    private final String type;

    public WeComSender(Map<String, String> config) {
        appName = config.get(WeComAlertParamsConstants.WALMART_API_APPNAME);
        format = config.get(WeComAlertParamsConstants.WALMART_API_FORMAT);
        url = config.get(WeComAlertParamsConstants.WALMART_API_URL);
        accessToken = config.get(WeComAlertParamsConstants.WALMART_API_ACCESS_TOKEN);
        addressList = getAddressList(config.get(WeComAlertParamsConstants.WECOM_ROBOT_ADDRESS_LIST));
        businessSubject = config.get(WeComAlertParamsConstants.WECOM_ROBOT_BUSINESS_SUBJECT);
        type = config.get(WeComAlertParamsConstants.WECOM_ROBOT_TYPE);
    }

    public List<String> getAddressList(String addressStr) {
        return Arrays.stream(addressStr.split(","))
                .distinct()
                .collect(Collectors.toList());
    }

    public AlertResult send(String title, String content) {
        AlertResult result = new AlertResult();

        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("content", content);
        contentMap.put("contentType", "");
        contentMap.put("title", title);

        Map<String, Object> apiParam = new HashMap<>();
        apiParam.put(WeComAlertParamsConstants.WECOM_ROBOT_ADDRESS_LIST, addressList);
        apiParam.put(WeComAlertParamsConstants.WALMART_API_APPNAME, appName);
        apiParam.put(WeComAlertParamsConstants.WECOM_ROBOT_BUSINESS_SUBJECT, businessSubject);
        apiParam.put(WeComAlertParamsConstants.WECOM_ROBOT_TYPE, type);
        apiParam.put(WeComAlertConstants.CONTENT_BODY, contentMap);

        Map<String, Object> walmartParam = new HashMap<>();
        walmartParam.put(WeComAlertParamsConstants.WALMART_API_APPNAME, appName);
        walmartParam.put(WeComAlertParamsConstants.WALMART_API_FORMAT, format);
        walmartParam.put("sign", "");
        walmartParam.put("source", "");
        walmartParam.put("timestamp", "");
        walmartParam.put("version", "");
        walmartParam.put("param", apiParam);

        try {
            HttpUtils.doPost(url, walmartParam, accessToken, appName);
            result.setStatus("true");
            result.setMessage("wecom send message success");
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
            result.setMessage("false");
            result.setMessage("wecom send message failed, because of " + e.getMessage());
        }
        return result;
    }


}

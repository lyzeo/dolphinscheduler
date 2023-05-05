package org.apache.dolphinscheduler.plugin.task.http.plugins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.dolphinscheduler.plugin.task.http.HttpParametersType;
import org.apache.dolphinscheduler.plugin.task.http.HttpProperty;
import org.apache.dolphinscheduler.plugin.task.http.utils.SignUtils;
import org.apache.dolphinscheduler.spi.utils.JSONUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.dolphinscheduler.plugin.task.http.HttpTaskConstants.APPLICATION_JSON;

public class AlohaPlugin extends AuthenticationApi {

    private static final String PARAM_TIMESTAMP = "timestamp";

    @Override
    public CloseableHttpResponse sendRequest() throws IOException {
        return super.sendRequest();
    }

    @Override
    protected void addRequestParams(RequestBuilder builder, List<HttpProperty> httpPropertyList) {
        if (CollectionUtils.isNotEmpty(httpPropertyList)) {
            ObjectNode jsonParam = JSONUtils.createObjectNode();
            for (HttpProperty property : httpPropertyList) {
                if (property.getHttpParametersType() != null) {
                    if (property.getHttpParametersType().equals(HttpParametersType.PARAMETER)) {
                        builder.addParameter(property.getProp(), property.getValue());
                    } else if (property.getHttpParametersType().equals(HttpParametersType.BODY)) {
                        jsonParam.put(property.getProp(), property.getValue());
                    }
                }
            }

            Map<String, String> map = paramReplace(httpPropertyList);
            jsonParam.put(PARAM_TIMESTAMP, map.get(PARAM_TIMESTAMP));
            jsonParam.put("sign", map.get("sign"));

            String reverse = jsonParam.get("param").toString();
            ObjectNode reverseObj = JSONUtils.parseObject(reverse);
            jsonParam.replace("param", reverseObj);

            StringEntity postingString = new StringEntity(jsonParam.toString(), Charsets.UTF_8);
            postingString.setContentEncoding(StandardCharsets.UTF_8.name());
            postingString.setContentType(APPLICATION_JSON);
            builder.setEntity(postingString);
        }
    }

    /**
     * 参数替换
     * @param httpPropertyList 请求参数集合
     * @return
     */
    public Map<String , String > paramReplace(List<HttpProperty> httpPropertyList) {
        Map<String, String> map = new HashMap<>();
        for (HttpProperty httpProperty : httpPropertyList) {
            if (httpProperty.getHttpParametersType().equals(HttpParametersType.BODY)) {
                map.put(httpProperty.getProp(), httpProperty.getValue());
            }
        }
        long timestamp = System.currentTimeMillis();
        String sign = SignUtils.createSign(map, Long.toString(timestamp));

        // 清空map集合
        map.clear();
        // 将签名和时间戳存入map作为返回值
        map.put("sign", sign);
        map.put(PARAM_TIMESTAMP, Long.toString(timestamp));
        return map;
    }
}

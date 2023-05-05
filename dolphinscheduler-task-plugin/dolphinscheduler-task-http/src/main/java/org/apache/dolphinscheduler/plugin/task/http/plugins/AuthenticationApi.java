package org.apache.dolphinscheduler.plugin.task.http.plugins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.dolphinscheduler.plugin.task.http.HttpParameters;
import org.apache.dolphinscheduler.plugin.task.http.HttpParametersType;
import org.apache.dolphinscheduler.plugin.task.http.HttpProperty;
import org.apache.dolphinscheduler.spi.task.Property;
import org.apache.dolphinscheduler.spi.task.paramparser.ParamUtils;
import org.apache.dolphinscheduler.spi.task.paramparser.ParameterUtils;
import org.apache.dolphinscheduler.spi.task.request.TaskRequest;
import org.apache.dolphinscheduler.spi.utils.JSONUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.apache.dolphinscheduler.plugin.task.http.HttpTaskConstants.APPLICATION_JSON;

public abstract class AuthenticationApi {

    protected static final Logger logger = LoggerFactory.getLogger(AuthenticationApi.class);

    protected RequestBuilder requestBuilder;

    protected CloseableHttpClient client;

    protected TaskRequest taskExecutionContext;

    protected HttpParameters httpParameters;

    protected Map<String, Property> paramsMap;

    protected List<HttpProperty> httpPropertyList;

    public void init(RequestBuilder requestBuilder,
                      TaskRequest taskExecutionContext,
                      HttpParameters httpParameters,
                      Map<String, Property> paramsMap,
                      List<HttpProperty> httpPropertyList) {
        this.requestBuilder = requestBuilder;
        this.taskExecutionContext = taskExecutionContext;
        this.httpParameters = httpParameters;
        this.paramsMap = paramsMap;
        this.httpPropertyList = httpPropertyList;
        this.client = createHttpClient();
    }

    protected AuthenticationApi() {
    }

    public CloseableHttpResponse sendRequest() throws IOException {
        // replace placeholder,and combine local and global parameters
        addRequestParams(requestBuilder, httpPropertyList);
        String requestUrl = ParameterUtils.convertParameterPlaceholders(httpParameters.getUrl(), ParamUtils.convert(paramsMap));
        HttpUriRequest request = requestBuilder.setUri(requestUrl).build();
        setHeaders(request, httpPropertyList);
        return client.execute(request);
    }

    /**
     * create http client
     *
     * @return CloseableHttpClient
     */
    protected CloseableHttpClient createHttpClient() {
        final RequestConfig requestConfig = requestConfig();
        HttpClientBuilder httpClientBuilder;
        httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(requestConfig);
        return httpClientBuilder.build();
    }

    /**
     * request config
     *
     * @return RequestConfig
     */
    private RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setSocketTimeout(httpParameters.getSocketTimeout())
                .setConnectTimeout(httpParameters.getConnectTimeout())
                .build();
    }

    /**
     * add request params
     *
     * @param builder buidler
     * @param httpPropertyList http property list
     */
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
            StringEntity postingString = new StringEntity(jsonParam.toString(), Charsets.UTF_8);
            postingString.setContentEncoding(StandardCharsets.UTF_8.name());
            postingString.setContentType(APPLICATION_JSON);
            builder.setEntity(postingString);
        }
    }

    /**
     * set headers
     *
     * @param request request
     * @param httpPropertyList http property list
     */
    protected void setHeaders(HttpUriRequest request, List<HttpProperty> httpPropertyList) {
        if (CollectionUtils.isNotEmpty(httpPropertyList)) {
            for (HttpProperty property : httpPropertyList) {
                if (HttpParametersType.HEADERS.equals(property.getHttpParametersType())) {
                    request.addHeader(property.getProp(), property.getValue());
                }
            }
        }
    }

}

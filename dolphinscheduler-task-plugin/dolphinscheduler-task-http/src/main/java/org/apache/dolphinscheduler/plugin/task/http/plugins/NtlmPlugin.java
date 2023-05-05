package org.apache.dolphinscheduler.plugin.task.http.plugins;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.dolphinscheduler.plugin.task.http.HttpParametersType;
import org.apache.dolphinscheduler.plugin.task.http.HttpProperty;
import org.apache.dolphinscheduler.spi.task.paramparser.ParamUtils;
import org.apache.dolphinscheduler.spi.task.paramparser.ParameterUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class NtlmPlugin extends AuthenticationApi {

    private static final Logger logger = LoggerFactory.getLogger(NtlmPlugin.class);

    @Override
    public CloseableHttpResponse sendRequest() throws IOException {
        HttpClientContext httpClientContext = new HttpClientContext();

        // replace placeholder,and combine local and global parameters
        addRequestParams(requestBuilder, httpPropertyList, httpClientContext);
        String requestUrl = ParameterUtils.convertParameterPlaceholders(httpParameters.getUrl(), ParamUtils.convert(paramsMap));
        HttpUriRequest request = requestBuilder.setUri(requestUrl).build();
        return client.execute(request, httpClientContext);
    }


    protected void addRequestParams(RequestBuilder builder,
                                    List<HttpProperty> httpPropertyList,
                                    HttpClientContext context) {
        if (CollectionUtils.isNotEmpty(httpPropertyList)) {
            String username = "";
            String password = "";

            for (HttpProperty httpProperty : httpPropertyList) {
                if (HttpParametersType.BODY.equals(httpProperty.getHttpParametersType())) {
                    if ("userName".equals(httpProperty.getProp())) {
                        username = httpProperty.getValue();
                    }

                    if ("password".equals(httpProperty.getProp())) {
                        password = httpProperty.getValue();
                    }
                }
            }
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new NTCredentials(username, password, "", ""));
            context.setCredentialsProvider(credentialsProvider);
            super.addRequestParams(builder, httpPropertyList);
        }
    }
}

package org.apache.dolphinscheduler.plugin.task.http.plugins;

import org.apache.dolphinscheduler.plugin.task.http.HttpProperty;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;

import java.io.IOException;
import java.util.List;

public class BasicPlugin extends AuthenticationApi {

    /**
     * send request
     *
     * @return CloseableHttpResponse
     * @throws IOException io exception
     */
    @Override
    public CloseableHttpResponse sendRequest() throws IOException {
        return super.sendRequest();
    }

    @Override
    protected void addRequestParams(RequestBuilder builder, List<HttpProperty> httpPropertyList) {
        super.addRequestParams(builder, httpPropertyList);
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.plugin.task.http;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.dolphinscheduler.plugin.task.api.AbstractTaskExecutor;
import org.apache.dolphinscheduler.plugin.task.http.plugins.AuthenticationApi;
import org.apache.dolphinscheduler.plugin.task.http.plugins.AuthenticationFactory;
import org.apache.dolphinscheduler.plugin.task.http.plugins.AuthenticationType;
import org.apache.dolphinscheduler.plugin.task.util.MapUtils;
import org.apache.dolphinscheduler.spi.task.AbstractParameters;
import org.apache.dolphinscheduler.spi.task.Property;
import org.apache.dolphinscheduler.spi.task.paramparser.ParamUtils;
import org.apache.dolphinscheduler.spi.task.paramparser.ParameterUtils;
import org.apache.dolphinscheduler.spi.task.request.TaskRequest;
import org.apache.dolphinscheduler.spi.utils.DateUtils;
import org.apache.dolphinscheduler.spi.utils.JSONUtils;
import org.apache.dolphinscheduler.spi.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTask extends AbstractTaskExecutor {

    /**
     * output
     */
    protected String output;
    /**
     * http parameters
     */
    private HttpParameters httpParameters;
    /**
     * taskExecutionContext
     */
    private TaskRequest taskExecutionContext;

    protected Map<String, Property> paramsMap;

    protected List<HttpProperty> httpPropertyList;

    /**
     * constructor
     *
     * @param taskExecutionContext taskExecutionContext
     */
    public HttpTask(TaskRequest taskExecutionContext) {
        super(taskExecutionContext);
        this.taskExecutionContext = taskExecutionContext;
    }

    @Override
    public void init() {
        logger.info("http task params {}", taskExecutionContext.getTaskParams());
        this.httpParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), HttpParameters.class);

        if (!httpParameters.checkParameters()) {
            throw new RuntimeException("http task params is not valid");
        }
        setParamMap();
        setHttpPropertyList();
    }

    @Override
    public void handle() throws Exception {

        long startTime = System.currentTimeMillis();
        String formatTimeStamp = DateUtils.formatTimeStamp(startTime);
        String statusCode = null;
        String body = null;

        AuthenticationApi api = AuthenticationFactory.getInstance(getAuthenticationType());
        api.init(createRequestBuilder(), taskExecutionContext, httpParameters, paramsMap, httpPropertyList);

        try (CloseableHttpResponse response = api.sendRequest()) {
            statusCode = String.valueOf(getStatusCode(response));
            body = getResponseBody(response);
            exitStatusCode = validResponse(body, statusCode);
            long costTime = System.currentTimeMillis() - startTime;
            logger.info("startTime: {}, httpUrl: {}, httpMethod: {}, costTime : {} milliseconds, statusCode : {}, body : {}, log : {}",
                    formatTimeStamp, httpParameters.getUrl(),
                    httpParameters.getHttpMethod(), costTime, statusCode, body, output);
        } catch (Exception e) {
            appendMessage(e.toString());
            exitStatusCode = -1;
            logger.error("httpUrl[" + httpParameters.getUrl() + "] connection failed：" + output, e);
            throw e;
        }

    }

    /**
     * get response body
     *
     * @param httpResponse http response
     * @return response body
     * @throws ParseException parse exception
     * @throws IOException io exception
     */
    protected String getResponseBody(CloseableHttpResponse httpResponse) throws ParseException, IOException {
        if (httpResponse == null) {
            return null;
        }
        HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            return null;
        }
        return EntityUtils.toString(entity, StandardCharsets.UTF_8.name());
    }

    /**
     * get status code
     *
     * @param httpResponse http response
     * @return status code
     */
    protected int getStatusCode(CloseableHttpResponse httpResponse) {
        return httpResponse.getStatusLine().getStatusCode();
    }

    /**
     * valid response
     *
     * @param body body
     * @param statusCode status code
     * @return exit status code
     */
    protected int validResponse(String body, String statusCode) {
        int exitStatusCode = 0;
        switch (httpParameters.getHttpCheckCondition()) {
            case BODY_CONTAINS:
                if (StringUtils.isEmpty(body) || !body.contains(httpParameters.getCondition())) {
                    appendMessage(httpParameters.getUrl() + " doesn contain "
                            + httpParameters.getCondition());
                    exitStatusCode = -1;
                }
                break;
            case BODY_NOT_CONTAINS:
                if (StringUtils.isEmpty(body) || body.contains(httpParameters.getCondition())) {
                    appendMessage(httpParameters.getUrl() + " contains "
                            + httpParameters.getCondition());
                    exitStatusCode = -1;
                }
                break;
            case STATUS_CODE_CUSTOM:
                if (!statusCode.equals(httpParameters.getCondition())) {
                    appendMessage(httpParameters.getUrl() + " statuscode: " + statusCode + ", Must be: " + httpParameters.getCondition());
                    exitStatusCode = -1;
                }
                break;
            default:
                if (!"200".equals(statusCode)) {
                    appendMessage(httpParameters.getUrl() + " statuscode: " + statusCode + ", Must be: 200");
                    exitStatusCode = -1;
                }
                break;
        }
        return exitStatusCode;
    }

    public String getOutput() {
        return output;
    }

    /**
     * append message
     *
     * @param message message
     */
    protected void appendMessage(String message) {
        if (output == null) {
            output = "";
        }
        if (message != null && !message.trim().isEmpty()) {
            output += message;
        }
    }

    /**
     * create request builder
     *
     * @return RequestBuilder
     */
    protected RequestBuilder createRequestBuilder() {
        if (httpParameters.getHttpMethod().equals(HttpMethod.GET)) {
            return RequestBuilder.get();
        } else if (httpParameters.getHttpMethod().equals(HttpMethod.POST)) {
            return RequestBuilder.post();
        } else if (httpParameters.getHttpMethod().equals(HttpMethod.HEAD)) {
            return RequestBuilder.head();
        } else if (httpParameters.getHttpMethod().equals(HttpMethod.PUT)) {
            return RequestBuilder.put();
        } else if (httpParameters.getHttpMethod().equals(HttpMethod.DELETE)) {
            return RequestBuilder.delete();
        } else {
            return null;
        }

    }

    @Override
    public AbstractParameters getParameters() {
        return this.httpParameters;
    }

    public AuthenticationType getAuthenticationType() {
        String authType = "";
        for (HttpProperty property : httpPropertyList) {
            if (HttpParametersType.HEADERS.equals(property.getHttpParametersType()) && ("authType".equals(property.getProp()))) {
                authType = property.getValue();
            }
        }
        return AuthenticationType.getByCode(authType);
    }

    public void setParamMap() {
        paramsMap = ParamUtils.convert(taskExecutionContext, this.httpParameters);
        if (MapUtils.isEmpty(paramsMap)) {
            paramsMap = new HashMap<>();
        }
        if (MapUtils.isNotEmpty(taskExecutionContext.getParamsMap())) {
            paramsMap.putAll(taskExecutionContext.getParamsMap());
        }
    }

    private void setHttpPropertyList() {
        List<HttpProperty> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(httpParameters.getHttpParams())) {
            for (HttpProperty httpProperty : httpParameters.getHttpParams()) {
                String jsonObject = JSONUtils.toJsonString(httpProperty);
                String params = ParameterUtils.convertParameterPlaceholders(jsonObject, ParamUtils.convert(paramsMap));
                logger.info("http request params：{}", params);
                result.add(JSONUtils.parseObject(params, HttpProperty.class));
            }
        }
        this.httpPropertyList = result;
    }
}

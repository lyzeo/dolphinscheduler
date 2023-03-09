package org.apache.dolphinscheduler.plugin.alert.walmart.wecom;

public final class WeComAlertConstants {

    static final String CONTENT_BODY = "contentBody";

    static final String HTTP_HEADER_ACCEPT = "accept";

    static final String HTTP_HEADER_CONTENT_TYPE = "Content-type";

    static final String HTTP_HEADER_ACCESS_TOKEN = "accessToken";

    static final String HTTP_HEADER_ALOHA_APP_NANE = "alohaAppName";

    static final String APPLICATION_JSON = "application/json";

    static final int TIMEOUT = 6000;

    private WeComAlertConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

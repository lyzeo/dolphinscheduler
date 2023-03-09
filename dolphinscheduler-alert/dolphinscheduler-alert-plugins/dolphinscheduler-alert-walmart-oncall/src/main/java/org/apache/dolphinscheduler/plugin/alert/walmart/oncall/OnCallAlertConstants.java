package org.apache.dolphinscheduler.plugin.alert.walmart.oncall;

public final class OnCallAlertConstants {

    static final String API_PARAM_HOSTNAME = "hostname";

    static final String API_PARAM_EVENTNAME = "eventname";

    static final String API_PARAM_SERVERITY = "serverity";

    static final String API_PARAM_STATUS = "status";

    static final String API_PARAM_EVENTDATE = "eventdate";

    static final String API_PARAM_EVENTTIME = "eventtime";

    static final String HOSTNAME_PREFIX = "cn_bigdata_meta_ds_alert_";

    static final String DATE_FORMAT = "yyyy.MM.dd";

    static final String TIME_FORMAT = "HH:mm:ss";

    static final String HOST = "taskHost";

    static final String HTTP_HEADER_ACCEPT = "Accept";

    static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    static final String APPLICATION_FORMAT = "application/json";

    static final int TIMEOUT = 6000;

    private OnCallAlertConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

package org.apache.dolphinscheduler.plugin.alert.walmart.wecom;

import org.apache.dolphinscheduler.alert.api.AlertChannel;
import org.apache.dolphinscheduler.alert.api.AlertData;
import org.apache.dolphinscheduler.alert.api.AlertInfo;
import org.apache.dolphinscheduler.alert.api.AlertResult;

import java.util.Map;

public final class WeComAlertChannel implements AlertChannel {

    @Override
    public AlertResult process(AlertInfo info) {
        AlertData alertData = info.getAlertData();
        Map<String, String> paramsMap = info.getAlertParams();
        if (null == paramsMap) {
            return new AlertResult("false", "wecom params is null");
        }
        return new WeComSender(info.getAlertParams()).send(alertData.getTitle(), alertData.getContent());
    }
}

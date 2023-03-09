package org.apache.dolphinscheduler.plugin.alert.walmart.oncall;

import org.apache.dolphinscheduler.alert.api.AlertChannel;
import org.apache.dolphinscheduler.alert.api.AlertData;
import org.apache.dolphinscheduler.alert.api.AlertInfo;
import org.apache.dolphinscheduler.alert.api.AlertResult;

import java.util.Map;

public class OnCallAlertChannel implements AlertChannel {

    @Override
    public AlertResult process(AlertInfo info) {
        AlertData data = info.getAlertData();
        Map<String, String> param = info.getAlertParams();
        if (null == param) {
            return new AlertResult("false", "oncall params is null");
        }
        return new OnCallSender(param).send(data.getTitle(), data.getContent());
    }
}

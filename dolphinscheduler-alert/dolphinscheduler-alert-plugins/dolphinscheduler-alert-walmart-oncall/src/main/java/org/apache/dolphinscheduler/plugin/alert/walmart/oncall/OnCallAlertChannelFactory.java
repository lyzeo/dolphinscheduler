package org.apache.dolphinscheduler.plugin.alert.walmart.oncall;

import org.apache.dolphinscheduler.alert.api.AlertChannel;
import org.apache.dolphinscheduler.alert.api.AlertChannelFactory;
import org.apache.dolphinscheduler.spi.params.base.ParamsOptions;
import org.apache.dolphinscheduler.spi.params.base.PluginParams;
import org.apache.dolphinscheduler.spi.params.base.Validate;
import org.apache.dolphinscheduler.spi.params.input.InputParam;
import org.apache.dolphinscheduler.spi.params.radio.RadioParam;

import java.util.Arrays;
import java.util.List;

public class OnCallAlertChannelFactory implements AlertChannelFactory {

    @Override
    public String name() {
        return "OnCall";
    }

    @Override
    public AlertChannel create() {
        return new OnCallAlertChannel();
    }

    @Override
    public List<PluginParams> params() {
        InputParam urlParam = InputParam.newBuilder(OnCallAlertParamsConstants.ONCALL_PARAMS_URL, OnCallAlertParamsConstants.ONCALL_PARAMS_URL)
                .setPlaceholder("please input oncall alert api")
                .addValidate(Validate.newBuilder().setRequired(true).build())
                .setInfo("oncall告警接口地址")
                .build();

        RadioParam serverityParam = RadioParam.newBuilder(OnCallAlertParamsConstants.ONCALL_PARAMS_SERVERITY, OnCallAlertParamsConstants.ONCALL_PARAMS_SERVERITY)
                .addParamsOptions(new ParamsOptions(Priority.HIGH.getDesc(), Priority.HIGH.getDesc(), false))
                .addParamsOptions(new ParamsOptions(Priority.DISASTER.getDesc(), Priority.DISASTER.getDesc(), false))
                .setValue(Priority.HIGH.getDesc())
                .setInfo("优先级")
                .addValidate(Validate.newBuilder().setRequired(true).build())
                .build();

        return Arrays.asList(urlParam, serverityParam);
    }
}

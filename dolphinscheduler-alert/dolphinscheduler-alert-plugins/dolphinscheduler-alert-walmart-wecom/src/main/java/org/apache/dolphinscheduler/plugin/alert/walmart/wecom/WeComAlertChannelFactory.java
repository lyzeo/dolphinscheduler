package org.apache.dolphinscheduler.plugin.alert.walmart.wecom;

import com.google.auto.service.AutoService;
import org.apache.dolphinscheduler.alert.api.AlertChannel;
import org.apache.dolphinscheduler.alert.api.AlertChannelFactory;
import org.apache.dolphinscheduler.spi.params.base.PluginParams;
import org.apache.dolphinscheduler.spi.params.base.Validate;
import org.apache.dolphinscheduler.spi.params.input.InputParam;

import java.util.Arrays;
import java.util.List;

@AutoService(AlertChannelFactory.class)
public final class WeComAlertChannelFactory implements AlertChannelFactory {

    @Override
    public String name() {
        return "WeCom";
    }

    @Override
    public AlertChannel create() {
        return new WeComAlertChannel();
    }

    @Override
    public List<PluginParams> params() {
        InputParam appNameParam = InputParam.newBuilder(WeComAlertParamsConstants.WALMART_API_APPNAME, WeComAlertParamsConstants.WALMART_API_APPNAME)
                .setPlaceholder("please input app name")
                .addValidate(Validate.newBuilder().setRequired(true).build())
                .build();

        InputParam formatParam = InputParam.newBuilder(WeComAlertParamsConstants.WALMART_API_FORMAT, WeComAlertParamsConstants.WALMART_API_FORMAT)
                .setPlaceholder("please input format, e.g. json")
                .addValidate(Validate.newBuilder().setRequired(true).build())
                .build();

        InputParam urlParam = InputParam.newBuilder(WeComAlertParamsConstants.WALMART_API_URL, WeComAlertParamsConstants.WALMART_API_URL)
                .setPlaceholder("please set format url, e.g. http://xxxx.com")
                .addValidate(Validate.newBuilder().setRequired(true).build())
                .build();

        InputParam tokenParam = InputParam.newBuilder(WeComAlertParamsConstants.WALMART_API_ACCESS_TOKEN, WeComAlertParamsConstants.WALMART_API_ACCESS_TOKEN)
                .setPlaceholder("")
                .addValidate(Validate.newBuilder().setRequired(true).build())
                .build();

        InputParam addressParam = InputParam.newBuilder(WeComAlertParamsConstants.WECOM_ROBOT_ADDRESS_LIST, WeComAlertParamsConstants.WECOM_ROBOT_ADDRESS_LIST)
                .setPlaceholder("")
                .addValidate(Validate.newBuilder().setRequired(true).build())
                .build();

        InputParam subjectParam = InputParam.newBuilder(WeComAlertParamsConstants.WECOM_ROBOT_BUSINESS_SUBJECT, WeComAlertParamsConstants.WECOM_ROBOT_BUSINESS_SUBJECT)
                .setValue("metadata platform alert")
                .setDisplay(true)
                .build();

        InputParam typeParam = InputParam.newBuilder(WeComAlertParamsConstants.WECOM_ROBOT_TYPE, WeComAlertParamsConstants.WECOM_ROBOT_TYPE)
                .setValue("wework")
                .setDisplay(true)
                .build();
        return Arrays.asList(appNameParam, formatParam, urlParam, tokenParam, addressParam, subjectParam, typeParam);
    }
}

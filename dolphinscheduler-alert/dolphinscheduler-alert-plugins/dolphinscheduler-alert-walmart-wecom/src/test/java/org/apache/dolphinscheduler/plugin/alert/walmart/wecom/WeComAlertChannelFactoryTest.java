package org.apache.dolphinscheduler.plugin.alert.walmart.wecom;

import org.apache.dolphinscheduler.alert.api.AlertChannel;
import org.apache.dolphinscheduler.spi.params.base.PluginParams;
import org.apache.dolphinscheduler.spi.utils.JSONUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class WeComAlertChannelFactoryTest {

    @Test
    public void testGetParam() {
        WeComAlertChannelFactory factory = new WeComAlertChannelFactory();
        List<PluginParams> params = factory.params();
        JSONUtils.toJsonString(params);
        Assert.assertEquals(7, params.size());
        System.out.println(JSONUtils.toJsonString(params));
    }

    @Test
    public void testCreate() {
        WeComAlertChannelFactory factory = new WeComAlertChannelFactory();
        AlertChannel alertChannel  = factory.create();
        Assert.assertNotNull(alertChannel);
    }
}

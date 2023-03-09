package org.apache.dolphinscheduler.plugin.alert.walmart.oncall;

import org.apache.dolphinscheduler.alert.api.AlertChannel;
import org.apache.dolphinscheduler.spi.params.base.PluginParams;
import org.apache.dolphinscheduler.spi.utils.JSONUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class OnCallAlertParamsConstantsTest {

    @Test
    public void testGetParams() {
        OnCallAlertChannelFactory factory = new OnCallAlertChannelFactory();
        List<PluginParams> params = factory.params();
        Assert.assertEquals(2, params.size());
        System.out.println(JSONUtils.toJsonString(params));
    }

    @Test
    public void testCreate() {
        OnCallAlertChannelFactory factory = new OnCallAlertChannelFactory();
        AlertChannel channel = factory.create();
        Assert.assertNotNull(channel);
    }

    @Test
    public void testToString() {
        OnCallAlertChannelFactory factory = null;
        System.out.println(factory.toString());
    }
}

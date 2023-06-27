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

package org.apache.dolphinscheduler.dao;

import org.apache.dolphinscheduler.common.enums.AlertStatus;
import org.apache.dolphinscheduler.common.enums.ProfileType;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.dao.entity.Alert;
import org.apache.dolphinscheduler.dao.entity.TaskInstance;
import org.apache.dolphinscheduler.dao.upgrade.UpgradeDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ActiveProfiles(ProfileType.H2)
@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootApplication
@Transactional
public class AlertDaoTest {
    @Autowired
    private AlertDao alertDao;


    @Test
    public void testAlertDao() {
        Alert alert = new Alert();
        alert.setTitle("Mysql Exception");
        alert.setContent("[\"alarm time：2018-02-05\", \"service name：MYSQL_ALTER\", \"alarm name：MYSQL_ALTER_DUMP\", "
            + "\"get the alarm exception.！，interface error，exception information：timed out\", \"request address：http://blog.csdn.net/dreamInTheWorld/article/details/78539286\"]");
        alert.setAlertGroupId(1);
        alert.setAlertStatus(AlertStatus.WAIT_EXECUTION);
        alertDao.addAlert(alert);

        List<Alert> alerts = alertDao.listPendingAlerts();
        Assert.assertNotNull(alerts);
        Assert.assertNotEquals(0, alerts.size());
    }

    @Test
    public void testSendServerStopedAlert() {
        int alertGroupId = 1;
        String host = "127.0.0.998165432";
        String serverType = "Master";
        alertDao.sendServerStopedAlert(alertGroupId, host, serverType);
        alertDao.sendServerStopedAlert(alertGroupId, host, serverType);
        long count = alertDao.listPendingAlerts()
                             .stream()
                             .filter(alert -> alert.getContent().contains(host))
                             .count();
        Assert.assertEquals(1L, count);
    }

    @Test
    public void upgradeDepTaskCode() {
            String taskParams = "{\n" +
                    "    \"conditionResult\":{\n" +
                    "        \"successNode\":[\n" +
                    "            \"\"\n" +
                    "        ],\n" +
                    "        \"failedNode\":[\n" +
                    "            \"\"\n" +
                    "        ]\n" +
                    "    },\n" +
                    "    \"dependence\":{\n" +
                    "        \"dependTaskList\":[\n" +
                    "            {\n" +
                    "                \"relation\":\"AND\",\n" +
                    "                \"dependItemList\":[\n" +
                    "                    {\n" +
                    "                        \"dateValue\":\"today\",\n" +
                    "                        \"cycle\":\"day\",\n" +
                    "                        \"projectCode\":49,\n" +
                    "                        \"definitionCode\":735,\n" +
                    "                        \"depTaskCode\":\"dwd_sams_all_action_dtl_di_3\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"relation\":\"AND\"\n" +
                    "    }\n" +
                    "}";
            Map<String, Object> map = JSONUtils.parseObject(taskParams, Map.class);
            Object dependence = map.get("dependence");
            Map<String, Object> dependMap = JSONUtils.parseObject(JSONUtils.toJsonString(dependence), Map.class);
            Object dependTaskList = dependMap.get("dependTaskList");
            List<Map<String, Object>> list = JSONUtils.parseObject(JSONUtils.toJsonString(dependTaskList),List.class);
            for (Map<String, Object> val : list) {
//                Map<String, String> dependTaskListMap = JSONUtils.parseObject(val, Map.class);
                Object dependItemList = val.get("dependItemList");
                List<Map<String, Object>> depItemMap = JSONUtils.parseObject(JSONUtils.toJsonString(dependItemList), List.class);
                for (Map<String, Object> stringStringMap : depItemMap) {
                    Object projectCode = stringStringMap.get("projectCode");
                    Object definitionCode = stringStringMap.get("definitionCode");
                    Object depTaskCode = stringStringMap.get("depTaskCode");
                }

            }

    }
}

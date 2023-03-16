package org.apache.dolphinscheduler.dao.upgrade.walmart;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dolphinscheduler.common.utils.ConnectionUtils;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.dao.entity.AlertGroup;
import org.apache.dolphinscheduler.dao.entity.AlertPluginInstance;
import org.apache.dolphinscheduler.dao.entity.ProcessDefinition;
import org.apache.dolphinscheduler.dao.entity.ProcessInstance;
import org.apache.dolphinscheduler.dao.mapper.AlertGroupMapper;
import org.apache.dolphinscheduler.dao.mapper.AlertPluginInstanceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WalmartUpgradeDao {

    private static final Logger log = LoggerFactory.getLogger(WalmartUpgradeDao.class);

    @Autowired
    private AlertPluginInstanceMapper alertPluginInstanceMapper;

    @Autowired
    private AlertGroupMapper alertGroupMapper;

    private static final String INSTANCE_NAME = "instance_name";

    private static final String GROUP_NAME = "group_name";

    private static final String RECEIVERS = "receivers";

    private static final String RECEIVERS_CC = "receivers_cc";

    private static final String WECHAT_ADDRESS = "wechat_address";

    private static final String ONCALL_SWITCH = "oncall_switch";

    public void upgradeAlert(Connection conn) {
        log.info("wal-mart upgrade alert start");
        List<Map<String, String>> processMap = selectProcessDefinition(conn);
        for (Map<String, String> item : processMap) {
            List<Integer> instanceIds = new ArrayList<>();

            String processId = item.get("id");
            if (StringUtils.isNotBlank(item.get(RECEIVERS))) {
                String receivers = item.get(RECEIVERS);
                String receiversCc = item.get(RECEIVERS_CC);
                instanceIds.add(insertEmailPluginInstance(processId, receivers, receiversCc));
            }

            String weChatAddresses = item.get(WECHAT_ADDRESS);
            if (StringUtils.isNotBlank(weChatAddresses)
                    && !"[]".equals(weChatAddresses)
                    && !"null".equals(weChatAddresses)
                    && !"[null]".equals(weChatAddresses)
                    && !"[\"null\"]".equals(weChatAddresses)) {
                instanceIds.add(insertWeComPluginInstance(processId, weChatAddresses));
            }

            String onCallSwitch = item.get(ONCALL_SWITCH);
            if (StringUtils.isNotBlank(onCallSwitch) && "1".equals(onCallSwitch)) {
                instanceIds.add(insertOnCallPluginInstance(processId));
             }

             if (CollectionUtils.isNotEmpty(instanceIds)) {
                 Integer alertGroupId = updateAlertGroup(processId, instanceIds);
                 updateProcessDefinition(conn, processId, alertGroupId);
                 updateScheduler(processId, conn, alertGroupId);
             }
        }
        log.info("walmart alert update end!!!");
    }

    public List<Map<String, String>> selectProcessDefinition(Connection conn) {
        List<Map<String, String>> result = new ArrayList<>();
        String sql = "select id, receivers, receivers_cc, wechat_address, oncall_switch from t_ds_process_definition";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, String> item = new HashMap<>();
                item.put("id", rs.getString(1));
                item.put(RECEIVERS, rs.getString(2));
                item.put(RECEIVERS_CC, rs.getString(3));
                item.put(WECHAT_ADDRESS, rs.getString(4));
                item.put(ONCALL_SWITCH, rs.getString(5));
                result.add(item);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("sql: " + sql, e);
        } finally {
            ConnectionUtils.releaseResource(rs, pstmt);
        }
        return result;
    }

    public Integer insertEmailPluginInstance(String processId, String receivers, String receiversCcs) {
        String instanceName = processId + "_" + "Email";

        AlertPluginInstance instance = new AlertPluginInstance();
        instance.setInstanceName(instanceName);
        instance.setPluginDefineId(14);
        instance.setPluginInstanceParams(JSONUtils.toJsonString(new EmailParam(receivers, receiversCcs)));
        alertPluginInstanceMapper.insert(instance);

        QueryWrapper<AlertPluginInstance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(INSTANCE_NAME, instanceName);
        return alertPluginInstanceMapper.selectOne(queryWrapper).getId();
    }

    public Integer insertWeComPluginInstance(String processId, String wechatAddress) {
        String instanceName = processId + "_" + "WeCom";

        AlertPluginInstance instance = new AlertPluginInstance();
        instance.setInstanceName(instanceName);
        instance.setPluginDefineId(20);
        instance.setPluginInstanceParams(JSONUtils.toJsonString(new WeComParam(wechatAddress)));
        alertPluginInstanceMapper.insert(instance);

        QueryWrapper<AlertPluginInstance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(INSTANCE_NAME, instanceName);
        return alertPluginInstanceMapper.selectOne(queryWrapper).getId();
    }

    public Integer insertOnCallPluginInstance(String processId) {
        String instanceName = processId + "_" + "OnCall";

        AlertPluginInstance instance = new AlertPluginInstance();
        instance.setInstanceName(instanceName);
        instance.setPluginDefineId(21);
        instance.setPluginInstanceParams(JSONUtils.toJsonString( new OnCallParam()));
        alertPluginInstanceMapper.insert(instance);

        QueryWrapper<AlertPluginInstance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(INSTANCE_NAME, instanceName);
        return alertPluginInstanceMapper.selectOne(queryWrapper).getId();
    }

    public Integer updateAlertGroup(String processId, List<Integer> instanceIds) {
        String groupName = processId + "_" + "alert";

        AlertGroup alertGroup = new AlertGroup();
        String ids = StringUtils.join(instanceIds, ",");
        alertGroup.setGroupName(groupName);
        alertGroup.setAlertInstanceIds(ids);
        alertGroup.setCreateUserId(1);
        alertGroupMapper.insert(alertGroup);

        QueryWrapper<AlertGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(GROUP_NAME, groupName);
        return alertGroupMapper.selectOne(queryWrapper).getId();
    }

    public void updateProcessDefinition(Connection conn, String processId, Integer alertGroupId) {
        String sql = "update t_ds_process_definition set warning_group_id=? where id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, alertGroupId);
            pstmt.setInt(2, Integer.parseInt(processId));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("sql: " + sql, e);
        }
    }

    public void updateScheduler(String processId, Connection conn, Integer alertGroupId) {
        String querySQL = "select id  from t_ds_schedules where process_definition_code = ?";
        String updateSQL = "update t_ds_schedules set warning_group_id = ? where id = ?";

        PreparedStatement queryStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            queryStmt = conn.prepareStatement(querySQL);
            updateStmt = conn.prepareStatement(updateSQL);

            queryStmt.setString(1, processId);
            log.info("{}, {}", querySQL, processId);
            rs = queryStmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString(1);
                if (StringUtils.isNotBlank(id)) {
                    updateStmt.setInt(1, alertGroupId);
                    updateStmt.setInt(2, Integer.parseInt(id));
                    log.info("sql: {}, {}, {}", updateSQL,  processId, alertGroupId);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("sql execute failed,", e);
        } finally {
            ConnectionUtils.releaseResource(queryStmt, rs, updateStmt);
        }
    }

    public void updateProcessInstanceVersion(Connection conn)  {
        List<ProcessInstance>  processInstances = getAllProcessInstance(conn);
        String sql = "update t_ds_process_instance set process_definition_version = ? where id = ?";

        for (ProcessInstance instance : processInstances) {
            ProcessDefinition definition = getProcessDefinitionByCode(conn, instance.getProcessDefinitionCode());
            try (PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setInt(1, definition.getVersion());
                pstmt.setLong(2, instance.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(sql, e);
            }
        }
    }

    private ProcessDefinition getProcessDefinitionByCode(Connection conn, Long processDefinitionCode) {
        String sql = "select id, name, version from t_ds_process_definition where code = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ProcessDefinition definition = new ProcessDefinition();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, processDefinitionCode);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                definition.setId(rs.getInt(1));
                definition.setName(rs.getString(2));
                definition.setVersion(rs.getInt(3));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(sql, e);
        } finally {
            ConnectionUtils.releaseResource(pstmt, rs);
        }
        return definition;
    }

    private List<ProcessInstance> getAllProcessInstance(Connection conn) {
        String sql = "select id, process_definition_code from t_ds_process_instance";
        List<ProcessInstance> result = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ProcessInstance instance = new ProcessInstance();
                instance.setId(rs.getInt(1));
                instance.setProcessDefinitionCode(rs.getLong(2));
                result.add(instance);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(sql, e);
        }
        return result;
    }

}

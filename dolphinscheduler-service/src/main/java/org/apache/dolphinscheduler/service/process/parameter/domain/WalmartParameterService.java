package org.apache.dolphinscheduler.service.process.parameter.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dolphinscheduler.common.process.Property;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.dao.entity.Command;
import org.apache.dolphinscheduler.dao.entity.ProcessDefinition;
import org.apache.dolphinscheduler.dao.mapper.parameter.WalmartParameterMapper;
import org.apache.dolphinscheduler.service.process.parameter.infrastructure.utils.ParameterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class WalmartParameterService {
    @Autowired
    private WalmartParameterMapper walmartParameterMapper;

    public Map<String, String> getAllGlobalParamMap(ProcessDefinition processDefinition) {
        if (Objects.nonNull(processDefinition.getGlobalParamMap())) {
            return processDefinition.getGlobalParamMap();
        }
        return new HashMap<>(0);
    }

    public List<Property> getAllGlobalParameter(ProcessDefinition processDefinition) {
        if (CollectionUtils.isNotEmpty(processDefinition.getGlobalParamList())) {
            return processDefinition.getGlobalParamList();
        }
        return new ArrayList<>(0);
    }

    public Map<String, String> getWalmartParamMap() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, String> params = walmartParameterMapper.selectByExecuteTime(sdf.format(date));
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey().substring(1);
            result.put(key, entry.getValue());
        }
        return result;
    }

    public List<Property> getWalmartParamList() {
        Map<String, String> paramMap = getWalmartParamMap();
        return ParameterUtils.convertToList(paramMap);
    }

    public Map<String, String> byExecuteTime(Date schedulerTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, String> params = walmartParameterMapper.selectByExecuteTime(sdf.format(schedulerTime));
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey().substring(1);
            result.put(key, entry.getValue());
        }
        return result;
    }

    public List<Property> byExecuteTimeToList(Date executeTime) {
        Map<String, String> paramMap = byExecuteTime(executeTime);
        return ParameterUtils.convertToList(paramMap);
    }

    public Map<String, String> getCommandParamMap(Command command) {
        if (StringUtils.isNotEmpty(command.getCommandParam())) {
            return JSONUtils.parseObject(command.getCommandParam(), new TypeReference<Map<String, String>>(){});
        }
        return new HashMap<>();
    }

    public List<Property> getCommandParamList(Command command) {
        Map<String, String> paramMap = getCommandParamMap(command);
        return ParameterUtils.convertToList(paramMap);
    }
}

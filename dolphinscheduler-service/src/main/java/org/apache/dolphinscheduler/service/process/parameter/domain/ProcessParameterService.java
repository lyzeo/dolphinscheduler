package org.apache.dolphinscheduler.service.process.parameter.domain;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dolphinscheduler.common.process.Property;
import org.apache.dolphinscheduler.dao.entity.Project;
import org.apache.dolphinscheduler.dao.entity.parameter.TaskParameter;
import org.apache.dolphinscheduler.dao.mapper.parameter.ProcessParameterMapper;
import org.apache.dolphinscheduler.dao.mapper.ProjectMapper;
import org.apache.dolphinscheduler.service.process.parameter.domain.parse.CalParser;
import org.apache.dolphinscheduler.service.process.parameter.domain.parse.NoCalParser;
import org.apache.dolphinscheduler.service.process.parameter.infrastructure.ParameterType;
import org.apache.dolphinscheduler.service.process.parameter.infrastructure.utils.ParameterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

@Component
public class ProcessParameterService {

    @Autowired
    private ProcessParameterMapper processParameterMapper;

    @Autowired
    private ProjectMapper projectMapper;

    public Map<String, String> byProcess(String processName, Long projectCode){
        return byProcessWithExecuteTime(processName, projectCode, null);
    }

    public List<Property> byProcessToList(String processName, Long projectCode) {
        Map<String, String> paramMap = byProcessWithExecuteTime(processName, projectCode, null);
        return ParameterUtils.convertToList(paramMap);
    }

    public Map<String, String> byProcessWithExecuteTime(String processName, Long projectCode , Date executeTime) {
        Map<String, String> result = new HashMap<>();
        Project project = projectMapper.queryByCode(projectCode);

        LambdaQueryWrapper<TaskParameter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskParameter::getTaskName, processName);
        queryWrapper.eq(TaskParameter::getProjectName, project.getName());
        List<TaskParameter> parameters = processParameterMapper.selectList(queryWrapper);
        for (TaskParameter item : parameters) {
            String value = item.getParamValue();
            if (ParameterType.EL_EXPRESSION.getCode().equals(item.getType())) {
                value = pareELExpressionWithExecuteTime(value, executeTime);
            }
            result.put(item.getParamName(), value);
        }
        return result;
    }

    public List<Property> byProcessWithExecuteTimeToList(String processName, Long projectCode, Date executeTime) {
        Map<String, String> paramMap = byProcessWithExecuteTime(processName, projectCode, executeTime);
        return ParameterUtils.convertToList(paramMap);
    }

    private String pareELExpressionWithExecuteTime(String expression, Date executeTime) {
        try {
            NoCalParser noCalParser = new NoCalParser();
            CalParser calParser = new CalParser();
            noCalParser.setNextParser(calParser);
            return noCalParser.parseExpression(expression, executeTime);
        } catch (NoSuchMethodException | ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}

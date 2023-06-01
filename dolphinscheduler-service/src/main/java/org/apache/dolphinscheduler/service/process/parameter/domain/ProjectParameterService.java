package org.apache.dolphinscheduler.service.process.parameter.domain;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dolphinscheduler.common.process.Property;
import org.apache.dolphinscheduler.dao.entity.Project;
import org.apache.dolphinscheduler.dao.entity.parameter.ProjectParameter;
import org.apache.dolphinscheduler.dao.mapper.ProjectMapper;
import org.apache.dolphinscheduler.dao.mapper.parameter.ProjectParameterMapper;
import org.apache.dolphinscheduler.service.process.parameter.domain.parse.CalParser;
import org.apache.dolphinscheduler.service.process.parameter.domain.parse.NoCalParser;
import org.apache.dolphinscheduler.service.process.parameter.infrastructure.ParameterType;
import org.apache.dolphinscheduler.service.process.parameter.infrastructure.utils.ParameterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

@Component
public class ProjectParameterService {
    @Autowired
    private ProjectParameterMapper projectParameterMapper;

    @Autowired
    private ProjectMapper projectMapper;

    public Map<String, String> byProject(Long projectCode) {
        return byProjectWithExecuteTime(projectCode, null);
    }

    public List<Property> byProjectToList(Long projectCode) {
        Map<String, String> paramMap = byProject(projectCode);
        return ParameterUtils.convertToList(paramMap);
    }

    public Map<String, String> byProjectWithExecuteTime(Long projectCode, Date executeTime) {
        Map<String, String> result = new HashMap<>();
        Project project = projectMapper.queryByCode(projectCode);

        LambdaQueryWrapper<ProjectParameter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectParameter::getProjectName, project.getName());
        List<ProjectParameter> projectParameters = projectParameterMapper.selectList(queryWrapper);

        for (ProjectParameter parameter : projectParameters) {
            String value = parameter.getParamValue();
            if (ParameterType.EL_EXPRESSION.getCode().equals(parameter.getType())) {
                 value = pareELExpressionWithExecuteTime(parameter.getParamValue(), executeTime);
            }
            result.put(parameter.getParamName(), value);
        }
        return result;
    }

    public List<Property> byProjectWithExecuteTimeToList(Long projectCode, Date executeTime) {
        Map<String, String> paramMap = byProjectWithExecuteTime(projectCode, executeTime);
        return ParameterUtils.convertToList(paramMap);
    }

    private String pareELExpressionWithExecuteTime(String expression, Date executeTime) {
        try {
            NoCalParser noCalParser = new NoCalParser();
            CalParser calParser = new CalParser();
            noCalParser.setNextParser(calParser);
            return noCalParser.parseExpression(expression, executeTime);
        } catch (NoSuchMethodException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

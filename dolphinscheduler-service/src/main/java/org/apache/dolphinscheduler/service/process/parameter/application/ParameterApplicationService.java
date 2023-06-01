package org.apache.dolphinscheduler.service.process.parameter.application;

import org.apache.dolphinscheduler.common.process.Property;
import org.apache.dolphinscheduler.dao.entity.Command;
import org.apache.dolphinscheduler.dao.entity.ProcessDefinition;
import org.apache.dolphinscheduler.dao.entity.ProcessInstance;
import org.apache.dolphinscheduler.dao.mapper.ProcessDefinitionMapper;
import org.apache.dolphinscheduler.service.process.parameter.domain.WalmartParameterService;
import org.apache.dolphinscheduler.service.process.parameter.domain.ProcessParameterService;
import org.apache.dolphinscheduler.service.process.parameter.domain.ProjectParameterService;
import org.apache.dolphinscheduler.service.process.parameter.domain.model.ParamItem;
import org.apache.dolphinscheduler.service.process.parameter.infrastructure.utils.ParameterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ParameterApplicationService {
    private final Logger logger = LoggerFactory.getLogger(ParameterApplicationService.class);

    @Autowired
    private WalmartParameterService walmartParameterService;

    @Autowired
    private ProjectParameterService projectParameterService;

    @Autowired
    private ProcessParameterService processParameterService;

    @Autowired
    private ProcessDefinitionMapper processDefinitionMapper;

    public List<Property> getAllParamList(ProcessDefinition processDefinition, Command command) {
        List<Property> result = new ArrayList<>(0);

        List<Property> globalParamList = walmartParameterService.getAllGlobalParameter(processDefinition);
        result.addAll(globalParamList);

        List<Property> projectParamList = projectParameterService.byProjectToList(processDefinition.getProjectCode());
        result = ParameterUtils.mergeList(result, projectParamList);
        logger.info("project param list: {}", projectParamList);

        List<Property> processParamList = processParameterService.byProcessToList(processDefinition.getName(), processDefinition.getProjectCode());
        result = ParameterUtils.mergeList(result, processParamList);
        logger.info("process param list: {}", processParamList);

        List<Property> walmartParamList = walmartParameterService.getWalmartParamList();
        result = ParameterUtils.mergeList(result, walmartParamList);
        logger.info("walmart param list: {}", walmartParamList);

        List<Property> commandParamList = walmartParameterService.getCommandParamList(command);
        result = ParameterUtils.mergeList(result, commandParamList);
        logger.info("command param list: {}", commandParamList);
        return result;
    }

    public Map<String, String> getAllParamMap(ProcessDefinition processDefinition, Command command) {
        Map<String, String> globalParamMap = walmartParameterService.getAllGlobalParamMap(processDefinition);
        Map<String, String> projectParamMap = projectParameterService.byProject(processDefinition.getProjectCode());
        Map<String, String> processParamMap = processParameterService.byProcess(processDefinition.getName(), processDefinition.getProjectCode());
        Map<String, String> walmartParamMap = walmartParameterService.getWalmartParamMap();
        Map<String, String> commandParamMap = walmartParameterService.getCommandParamMap(command);

        Map<String, String> result = new HashMap<>();
        result = ParameterUtils.mergeParamMap(result, globalParamMap);
        logger.info("merge with global param : {}", globalParamMap);

        result = ParameterUtils.mergeParamMap(result, projectParamMap);
        logger.info("merge with project param : {}", projectParamMap);

        result = ParameterUtils.mergeParamMap(result, processParamMap);
        logger.info("merge with process param : {}", processParamMap);

        result = ParameterUtils.mergeParamMap(result, walmartParamMap);
        logger.info("merge with walmart param : {}", walmartParamMap);

        result = ParameterUtils.mergeParamMap(result, commandParamMap);
        logger.info("merge with command param : {}", commandParamMap);

        return result;
    }

    public void replaceParamForRepeatRunning(ProcessInstance instance) {
        ProcessDefinition definition = processDefinitionMapper.queryByCode(instance.getProcessDefinitionCode());
        Date executeTime = Optional.ofNullable(instance.getScheduleTime()).orElse(instance.getCommandStartTime());

        Map<String, String> globalParamsMap = ParameterUtils.convertToMap(instance.getGlobalParams());
        Map<String, String> projectParamMap = projectParameterService.byProjectWithExecuteTime(definition.getProjectCode(), executeTime);
        Map<String, String> processParamMap = processParameterService.byProcessWithExecuteTime(definition.getName(), definition.getProjectCode(), executeTime);
        Map<String, String> walmartParamMap = walmartParameterService.byExecuteTime(executeTime);

        Map<String, String> result = new HashMap<>();
        result = ParameterUtils.mergeParamMap(result, globalParamsMap);
        logger.info("merge with global param for repeat running, data: {}", globalParamsMap);

        result = ParameterUtils.mergeParamMap(result, projectParamMap);
        logger.info("merge with project param for repeat running, data: {}", projectParamMap);

        result = ParameterUtils.mergeParamMap(result, processParamMap);
        logger.info("merge with process param for repeat running, data: {}", processParamMap);

        result = ParameterUtils.mergeParamMap(result, walmartParamMap);
        logger.info("merge with walmart param for repeat running, data: {}", walmartParamMap);

        String globalString = ParamItem.toStringByMap(result);
        logger.info("result {}", globalString);
        instance.setGlobalParams(globalString);
    }

    public List<Property> getForCompleteData(ProcessInstance instance) {
        List<Property> result = new ArrayList<>(0);
        Date executeTime = Optional.ofNullable(instance.getScheduleTime()).orElse(instance.getCommandStartTime());
        ProcessDefinition definition = processDefinitionMapper.queryByCode(instance.getProcessDefinitionCode());

        List<Property> projectParamList = projectParameterService.byProjectWithExecuteTimeToList(definition.getProjectCode(), executeTime);
        result = ParameterUtils.mergeList(result, projectParamList);
        logger.info("merge with project param for complete data, data: {}", projectParamList);

        List<Property> processParamList = processParameterService.byProcessWithExecuteTimeToList(definition.getName(), definition.getProjectCode(), executeTime);
        result = ParameterUtils.mergeList(result, processParamList);
        logger.info("merge with process param for complete data, data: {}", processParamList);

        List<Property> walmartParamList = walmartParameterService.byExecuteTimeToList(executeTime);
        result = ParameterUtils.mergeList(result, walmartParamList);
        logger.info("merge with walmart param for complete data, data: {}", walmartParamList);

        return result;
    }

}

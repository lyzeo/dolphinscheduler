package org.apache.dolphinscheduler.service.process.parameter.infrastructure.utils;

import org.apache.dolphinscheduler.common.enums.DataType;
import org.apache.dolphinscheduler.common.process.Property;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.service.process.parameter.domain.model.ParamItem;

import java.util.*;

public class ParameterUtils {
    private ParameterUtils() {
        throw new AssertionError();
    }

    public static Map<String, String> convertToMap(String paramString) {
        Map<String, String> result = new HashMap<>();
        List<ParamItem>  paramItems = JSONUtils.toList(paramString, ParamItem.class);
        for (ParamItem item : paramItems) {
            result.put(item.getProp(), item.getValue());
        }
        return result;
    }

    public static Map<String, String> mergeParamMap(Map<String, String> sourceMap, Map<String, String> targetMap) {
        Map<String, String> result = new HashMap<>(targetMap);
        for (Map.Entry<String, String> item : sourceMap.entrySet()) {
            result.merge(item.getKey(), item.getValue(), (v1, v2) -> v1);
        }
        return result;
    }

    public static List<Property> convertToList(Map<String, String> paramMap) {
        List<Property> result = new ArrayList<>(0);
        if (Objects.nonNull(paramMap)) {
            for (Map.Entry<String, String> item : paramMap.entrySet()) {
                Property property = new Property(item.getKey(), null, DataType.VARCHAR, item.getValue());
                result.add(property);
            }
        }
        return result;
    }

    public static List<Property> mergeList(List<Property> sourceList, List<Property> targetList) {
        List<ParamItem> targetItems = ParamItem.createByProperty(targetList);
        Set<ParamItem> set = new LinkedHashSet<>(targetItems);

        List<Property> result = new ArrayList<>(targetList);
        for (Property item : sourceList) {
            ParamItem model = ParamItem.createByProperty(item);
            if (set.add(model)) {
                result.add(item);
            }
        }
        return result;
    }

}

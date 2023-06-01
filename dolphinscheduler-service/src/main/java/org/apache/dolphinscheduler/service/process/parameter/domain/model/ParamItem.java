package org.apache.dolphinscheduler.service.process.parameter.domain.model;

import org.apache.dolphinscheduler.common.process.Property;
import org.apache.dolphinscheduler.common.utils.JSONUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParamItem implements Serializable {
    private String prop;

    private String value;

    public ParamItem() {
    }

    public ParamItem(String prop, String value) {
        this.prop = prop;
        this.value = value;
    }

    public static String toStringByMap(Map<String, String> paramMap) {
        List<ParamItem> result = new ArrayList<>(0);
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            ParamItem paramItem = new ParamItem(entry.getKey(), entry.getValue());
            result.add(paramItem);
        }
        return JSONUtils.toJsonString(result);
    }

    public static ParamItem createByProperty(Property property) {
        return new ParamItem(property.getProp(), property.getValue());
    }

    public static List<ParamItem> createByProperty(List<Property> property) {
        List<ParamItem> result = new ArrayList<>(0);
        for (Property item : property) {
            ParamItem paramItem = createByProperty(item);
            result.add(paramItem);
        }
        return result;
    }

    public Property toProperty() {
        return new Property(prop, null, null, value);
    }

    @Override
    public int hashCode() {
        String in = prop;
        return in.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass()) {
            return false;
        }

        ParamItem paramItem = (ParamItem) obj;
        return prop.equals(paramItem.prop);
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

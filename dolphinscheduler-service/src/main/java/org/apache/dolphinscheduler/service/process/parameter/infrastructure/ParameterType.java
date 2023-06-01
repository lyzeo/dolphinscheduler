package org.apache.dolphinscheduler.service.process.parameter.infrastructure;

public enum ParameterType {
    CONSTANT("1", "constant"),
    EL_EXPRESSION("3", "el_expression");
    private final String code;
    private final String desc;

    ParameterType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

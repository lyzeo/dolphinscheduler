package org.apache.dolphinscheduler.plugin.alert.walmart.oncall;

public enum StatusType {

    RESOLVED(0, "resolved"),
    SIREN(1, "siren")
    ;

    private final int code;

    private final String desc;

    StatusType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

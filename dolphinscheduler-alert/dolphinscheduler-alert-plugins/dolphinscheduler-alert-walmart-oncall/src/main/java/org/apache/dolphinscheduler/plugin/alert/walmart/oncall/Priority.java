package org.apache.dolphinscheduler.plugin.alert.walmart.oncall;

public enum Priority {

    HIGH(0, "High"),
    DISASTER(1, "Disater")
    ;

    private final int code;

    private final String desc;

    Priority(int code, String desc) {
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

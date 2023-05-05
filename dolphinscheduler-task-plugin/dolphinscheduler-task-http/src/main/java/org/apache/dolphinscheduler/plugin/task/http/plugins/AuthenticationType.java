package org.apache.dolphinscheduler.plugin.task.http.plugins;

import java.util.Arrays;

public enum AuthenticationType {
    BASIC("0", "basic"),
    ALOHA("3", "aloha"),
    NTLM("4", "Ntlm"),
    POWER_BI("5", "PowerBI");

    private final String code;

    private final String description;

    AuthenticationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }


    public static AuthenticationType getByCode(String code) {
        return Arrays.stream(AuthenticationType.values())
                .filter(it-> it.getCode().equals(code))
                .findFirst()
                .orElseGet(() -> BASIC);
    }


}

package org.apache.dolphinscheduler.plugin.task.http.plugins;

public class AuthenticationFactory {

    public static AuthenticationApi getInstance(AuthenticationType type) {
        switch (type) {
            case ALOHA: return new AlohaPlugin();
            case NTLM:
            case POWER_BI: return new NtlmPlugin();
            case BASIC:
            default: return new BasicPlugin();
        }
    }

}

package org.apache.dolphinscheduler.plugin.task.http.utils;

import java.util.Map;

public class SignUtils {

    private SignUtils() {
        throw new UnsupportedOperationException("util class can not support init");
    }

    /**
     * 生成 签名
     * @param map 参数
     * @param timestamp 时间戳
     * @return sign
     */
    public static String createSign(Map<String, String> map, String timestamp) {
        String appName = map.get("appName");
        String appSecret = map.get("appSecret");
        String source = map.get("source");
        String format = map.get("format");
        String version = map.get("version");
        String param = Encryption.getMapSortFieldJson(map.get("param").replace(" ", ""));
        String beforeSign = appSecret + appName + source + timestamp + format + version + param + appSecret;
        return Encryption.sha512(beforeSign).toUpperCase();
    }

}

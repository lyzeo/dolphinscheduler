package org.apache.dolphinscheduler.plugin.task.http.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.apache.dolphinscheduler.spi.utils.JSONUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {

    private Encryption() {
        throw new UnsupportedOperationException("util class can not supported defined");
    }

    /**
     * decrypt code
     * @param text
     * @return
     */
    public static String sha512(String text) {
        MessageDigest sha512 = null;
        try {
            sha512 = MessageDigest.getInstance("SHA-512");
            sha512.update(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert sha512 != null;
        return convertByteToHex(sha512.digest()).toUpperCase();
    }

    /**
     *
     * @param data json byte
     * @return json string
     */
    public static String convertByteToHex(byte[] data) {
        StringBuilder hexData = new StringBuilder();
        for (byte datum : data) {
            hexData.append(Integer.toString((datum & 0xff) + 0x100, 16).substring(1));
        }
        return hexData.toString();
    }

    /**
     * JSON字符串排序
     *
     * @param json 原JSON字符串
     * @return 排序后的JSON字符串
     */
    public static String getMapSortFieldJson(String json) {
        if (StringUtils.isEmpty(json)) {
            return json;
        }

        ObjectNode object = JSONUtils.parseObject(json);
        if (object != null) {
            json = getMapSortObjectJson(object);
        }

        ArrayNode array = JSONUtils.parseArray(json);
        if (array != null) {
            json = getMapSortArrayJson(array);
        }
        return json;
    }

    /**
     * JSONArray转换为排序后的字符串
     *
     * @param jsonArray
     * @return
     */
    public static String getMapSortArrayJson(ArrayNode jsonArray) {
        ArrayNode resultArray = JsonNodeFactory.instance.arrayNode();

        for (int index = 0; index < jsonArray.size(); index++) {
            //3、把里面的对象转化为JSONObject
            String value = jsonArray.get(index).toString();

            ObjectNode valueObject = validObject(value);
            ArrayNode valueArray = validArray(value);

            if (valueObject != null || valueArray != null) {
                resultArray.add(JSONUtils.toJsonNode(getMapSortFieldJson(value)));
                continue;
            }

            if (isValid(value)) {
                resultArray.add(jsonArray.get(index));
                continue;
            }
            resultArray.add(value);
        }
        return JSONUtils.toJsonString(resultArray, SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS);
    }

    /**
     * JSONObject转换为排序后的字符串
     *
     * @param jsonObject
     * @return
     */
    public static String getMapSortObjectJson(ObjectNode jsonObject) {
        if (null == jsonObject) {
            return "";
        }

        for (JsonNode jsonNode : jsonObject) {
            String key = jsonNode.toString();
            String value = jsonObject.get(key).toString();

            ObjectNode valueObject = validObject(value);
            ArrayNode valueArray = validArray(value);

            // 如果不是JSONArray或JSONObject，则跳过
            if (valueObject == null || valueArray == null) {
                continue;
            }

            String mapJson = getMapSortObjectJson(valueObject);
            jsonObject.set(key, JSONUtils.parseObject(mapJson));

            String arrayJson = getMapSortArrayJson(valueArray);
            jsonObject.set(key, JSONUtils.parseArray(arrayJson));
        }
        return JSONUtils.toJsonString(jsonObject, SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
    }

    public static ObjectNode validObject(String text) {
        try {
            ObjectNode valueObject = JSONUtils.parseObject(text);
            if (valueObject == null || valueObject.isEmpty()) {
                return null;
            }
            return valueObject;
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static ArrayNode validArray(String text) {
        try {
            ArrayNode valueArray = JSONUtils.parseArray(text);
            if (valueArray == null || valueArray.isEmpty()) {
                return null;
            }
            return valueArray;
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static boolean isValid(String text) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(text);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}
